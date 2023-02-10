package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class representing client to REST server.
 */
public class RestClient {
    private URL baseURL;


    public RestClient(URL baseURL) {
        this.baseURL = baseURL;
    }


    /**
     * Retrieves and deserializes json file from REST server
     *
     * @param fromEnd Desired endpoint as String
     * @param tClass  Class to map to
     * @return Deserialized data as desired class
     */
    public <T> T deserialize(String fromEnd, Class<T> tClass) {
        URL completeURl = null;
        T response = null;
        try {
            String tmpURL = baseURL.toString();
            if (tmpURL.endsWith("/") && fromEnd.startsWith("/")) {
                fromEnd.substring(1);
            } else if (!tmpURL.endsWith("/") && !fromEnd.startsWith("/")) {
                tmpURL += "/";
            }
            completeURl = new URL(tmpURL + fromEnd);
        } catch (MalformedURLException ex) {
            System.err.println("Provided URL invalid : " + baseURL + fromEnd);
            System.exit(2);
        }
        try {
            response = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(completeURl, tClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }


}

package uk.ac.ed.inf;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class WriteDeliveries {
    private static JSONObject orderInfo(Order order){
        JSONObject object = new JSONObject();
        object.put("orderNo", order.getOrderNo());
        object.put("outcome", order.getOrderOutcome().toString());
        object.put("costInPence", order.deliveryCostInPence());
        return object;
    }
    private static JSONArray deliveries(List<Order> orderList){
        JSONArray deliveriesArray = new JSONArray();
        for(Order order: orderList){
            deliveriesArray.add(orderInfo(order));
        }
        return deliveriesArray;
    }

    public static void writeFile(String date, List<Order> orderList) throws IOException {
        String path = "deliveries-" + date + ".json";
        FileWriter fileWriter = new FileWriter(path);
        fileWriter.write(deliveries(orderList).toJSONString());
        fileWriter.close();
    }
}


package uk.ac.ed.inf;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Record storing the information of an item on a menu as an object.
 *
 * @param item  Name of item as a String
 * @param price Price in pence of item as an int
 * @author fionahamlin
 */
public record Menu(@JsonProperty("name") String item, @JsonProperty("priceInPence") Integer price) {
}

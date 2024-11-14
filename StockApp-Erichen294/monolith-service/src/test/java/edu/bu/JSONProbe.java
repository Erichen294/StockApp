package edu.bu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Probe")
public class JSONProbe {

  static final String PROBE_STRING =
      "{\"data\":" + "[" + "{\"p\":215,\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";

  @Test
  public void jsonProbe() throws ParseException {
    // For this problem, implement the jsonProbe method to print out,
    // via System.out.println, the price value parsed out of the single
    // data point in the response defined in PROBE_STRING
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = (JSONObject) parser.parse(PROBE_STRING);
    JSONArray dataArray = (JSONArray) jsonObject.get("data");
    JSONObject dataPoint = (JSONObject) dataArray.get(0);
    Long price = (Long) dataPoint.get("p");
    System.out.println("Price: " + price);
  }

  @Test
  public void jsonProbe_noPrice() throws ParseException {
    // For this problem, implement jsonProbe_noPrice to run the same code that you had above against
    // an
    // input string that does not have the p key in the trade dictionary. How does your probe code
    // behave
    // when price is missing?
    JSONParser parser = new JSONParser();
    String noPriceString =
        "{\"data\":" + "[" + "{\"s\":\"AAPL\",\"t\":1704067200000,\"v\":1}," + "]" + "}";
    JSONObject jsonObject = (JSONObject) parser.parse(noPriceString);
    JSONArray dataArray = (JSONArray) jsonObject.get("data");
    JSONObject dataPoint = (JSONObject) dataArray.get(0);
    Long price = (Long) dataPoint.get("p");
    System.out.println("Price: " + price);
  }

  @Test
  public void jsonProbe_forceThrow() throws ParseException {
    // Your goal in jsonProbe_forceThrow is to pass a String to .parse() that causes JSONParser to
    // throw an exception
    JSONParser parser = new JSONParser();
    String invalidJson = "{\"data\": [ {\"p\":215, \"s\":\"AAPL\" ";
    try {
      parser.parse(invalidJson);
    } catch (ParseException e) {
      System.out.println("Caught exception: " + e.getMessage());
    }
  }
}

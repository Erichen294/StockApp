package edu.bu.finhub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/** Parses updates that arrive from FinHub around stock updates. */
public class FinhubParser {
  public @Nullable List<FinhubResponse> parse(String response) {
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = null;

    try {
      jsonObject = (JSONObject) parser.parse(response);
    } catch (ParseException e) {
      return new ArrayList<>();
    }

    List<FinhubResponse> results = new ArrayList<>();

    JSONArray dataArray = (JSONArray) jsonObject.get("data");

    // ignore pings
    if (dataArray == null) {
      return new ArrayList<>();
    }

    for (Object dataPoint : dataArray) {
      Map jsonDataPoint = (Map) dataPoint;

      String symbol = (String) jsonDataPoint.get("s");
      Double price =
          (jsonDataPoint.get("p") instanceof Double)
              ? (Double) jsonDataPoint.get("p")
              : (Long) jsonDataPoint.get("p");
      Long msSinceEpoch = (Long) jsonDataPoint.get("t");
      Long volume = (Long) jsonDataPoint.get("v");

      results.add(new FinhubResponse(symbol, price, msSinceEpoch, volume));
    }

    return results;
  }
}

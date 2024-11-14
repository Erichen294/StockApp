package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import org.json.simple.JSONObject;

/** Handler for processing HTTP requests to determine the most active stock */
public class MostActiveStockHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final HttpUtility httpUtility = new HttpUtility();

  public MostActiveStockHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String mostActive = analyticsComputor.mostActiveStock();
    JSONObject jsonResponse = new JSONObject();
    jsonResponse.put("mostActiveStock", mostActive);

    // adding application/json to Content-Type
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    httpUtility.sendResponse(exchange, jsonResponse.toJSONString(), 200);
  }
}

package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.InvalidSymbolException;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.tinylog.Logger;

/** Handler for average volume updates arriving via WebSocket callbacks from Finhub. */
public class AverageVolumeHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final HttpUtility httpUtility = new HttpUtility();

  public AverageVolumeHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    JSONObject jsonResponse = new JSONObject();
    int statusCode;
    try {
      double averageVolume = analyticsComputor.averageVolumePerSecond(symbol);
      jsonResponse.put("symbol", symbol);
      jsonResponse.put("averageVolumePerSecond", averageVolume);
      statusCode = 200;
    } catch (UnknownSymbolException | InvalidSymbolException e) {
      jsonResponse.put("error", e.getMessage());
      statusCode = 404;
    }

    Logger.info(
        "Handled average volume request for {}, responding with {}.",
        symbol,
        jsonResponse.toJSONString());

    // adding application/json to Content-Type
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    httpUtility.sendResponse(exchange, jsonResponse.toJSONString(), statusCode);
  }
}

package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.UnknownSymbolException;
import edu.bu.metrics.MetricsTracker;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.tinylog.Logger;

/** Handler for price updates arriving via WebSocket callbacks from Finhub. */
public class PriceHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final MetricsTracker metricsTracker;
  final HttpUtility httpUtility = new HttpUtility();

  public PriceHandler(AnalyticsComputor analyticsComputor, MetricsTracker metricsTracker) {
    this.analyticsComputor = analyticsComputor;
    this.metricsTracker = metricsTracker;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    JSONObject jsonResponse = new JSONObject();
    int statusCode;
    try {
      Double currentPrice = analyticsComputor.currentPrice(symbol);
      jsonResponse.put("symbol", symbol);
      jsonResponse.put("currentPrice", currentPrice);
      statusCode = 200;
    } catch (UnknownSymbolException e) {
      jsonResponse.put("error", e.getMessage());
      statusCode = 404;
    }

    metricsTracker.incrementPriceRequestCount(symbol);

    Logger.info(
        "Handled price request for {}, responding with {}.", symbol, jsonResponse.toJSONString());
    // adding application/json to Content-Type
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    httpUtility.sendResponse(exchange, jsonResponse.toJSONString(), statusCode);
  }
}

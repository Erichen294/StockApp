package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.analytics.AnalyticsComputor;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import java.util.Set;
import org.json.simple.JSONArray;

/** Supports the symbols HTTP endpoint. */
public class SymbolListHandler implements HttpHandler {
  final AnalyticsComputor analyticsComputor;
  final HttpUtility httpUtility = new HttpUtility();

  public SymbolListHandler(AnalyticsComputor analyticsComputor) {
    this.analyticsComputor = analyticsComputor;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Set<String> symbols = analyticsComputor.knownSymbols();
    JSONArray jsonResponse = new JSONArray();
    jsonResponse.addAll(symbols);
    // adding application/json to Content-Type
    exchange.getResponseHeaders().add("Content-Type", "application/json");
    httpUtility.sendResponse(exchange, jsonResponse.toJSONString(), 200);
  }
}

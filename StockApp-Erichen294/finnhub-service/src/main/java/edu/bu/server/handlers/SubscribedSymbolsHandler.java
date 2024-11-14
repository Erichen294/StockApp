package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import java.util.Set;

/** Handler for subscribed symbols api arriving via WebSocket callbacks from Finhub. */
public class SubscribedSymbolsHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final HttpUtility httpUtility = new HttpUtility();

  public SubscribedSymbolsHandler(StockUpdatesClient stockUpdatesClient) {
    this.stockUpdatesClient = stockUpdatesClient;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Set<String> subscribedSymbols = stockUpdatesClient.getSubscribedSymbols();
    if (subscribedSymbols.isEmpty()) {
      String response = "StockApp is subscribed to 0 symbols.";
      httpUtility.sendResponse(exchange, response, 200);
    } else {
      buildSymbolsResponseMessageAndSend(subscribedSymbols, exchange);
    }
  }

  /**
   * Internal helper that sends responses for non-empty subscribedSymbols
   *
   * @param subscribedSymbols all subscribed stock symbols
   * @param exchange HttpExchange
   * @throws IOException Exception
   */
  public void buildSymbolsResponseMessageAndSend(
      Set<String> subscribedSymbols, HttpExchange exchange) throws IOException {
    int numOfSubscribedSymbols = subscribedSymbols.size();
    String response =
        "StockApp is subscribed to " + numOfSubscribedSymbols + " symbols." + " They are: ";
    for (String symbol : subscribedSymbols) {
      response += symbol + ", ";
    }
    // Remove last comma and space
    response = response.substring(0, response.length() - 2);
    response += ".";
    httpUtility.sendResponse(exchange, response, 200);
  }
}

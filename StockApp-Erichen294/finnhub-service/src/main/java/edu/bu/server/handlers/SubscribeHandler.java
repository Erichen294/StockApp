package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.utils.HttpUtility;
import edu.bu.utils.VerifyStockSymbol;
import java.io.IOException;
import java.util.Set;

/** Handler for subscribe api arriving via WebSocket callbacks from Finhub. */
public class SubscribeHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final VerifyStockSymbol verifyStockSymbol;
  final HttpUtility httpUtility;
  private final SymbolsPersistence symbolsPersistence;

  public SubscribeHandler(
      StockUpdatesClient stockUpdatesClient,
      VerifyStockSymbol verifyStockSymbol,
      SymbolsPersistence symbolsPersistence) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.verifyStockSymbol = verifyStockSymbol;
    httpUtility = new HttpUtility();
    this.symbolsPersistence = symbolsPersistence;
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // parse out symbol of interest from URL
    String[] requestURLParts = exchange.getRequestURI().getRawPath().split("/");
    String symbol = requestURLParts[requestURLParts.length - 1];

    if (!verifyStockSymbol.checkValidSymbol(symbol)) {
      String response = symbol + " is not a valid US Stock symbol";
      httpUtility.sendResponse(exchange, response, 400);
      return;
    }

    if (alreadySubscribedSendResponse(symbol, exchange)) {
      return;
    }

    if (aboveSubscribedLimitSendResponse(symbol, exchange)) {
      return;
    }

    // Subscribe to new symbol and add to persistence
    stockUpdatesClient.subscribeToSymbol(symbol);
    stockUpdatesClient.addSubscribedSymbolToSet(symbol);
    symbolsPersistence.add(symbol);

    // Respond with success
    String response = "StockApp is now subscribed to updates for " + symbol;
    httpUtility.sendResponse(exchange, response, 200);
  }

  /**
   * Internal helper that sends responses for already subscribed symbols
   *
   * @param symbol stock symbol of interest
   */
  public boolean alreadySubscribedSendResponse(String symbol, HttpExchange exchange)
      throws IOException {
    // Already subscribed
    Set<String> subscribedSymbols = stockUpdatesClient.getSubscribedSymbols();
    if (subscribedSymbols.contains(symbol)) {
      String response = symbol + " has already been registered";
      httpUtility.sendResponse(exchange, response, 409);
      return true;
    }
    return false;
  }

  /**
   * Internal helper that sends responses if there are already 10 subscribed symbols
   *
   * @param symbol stock symbol of interest
   */
  public boolean aboveSubscribedLimitSendResponse(String symbol, HttpExchange exchange)
      throws IOException {
    // Limit to 10 subscriptions
    Set<String> subscribedSymbols = stockUpdatesClient.getSubscribedSymbols();
    if (subscribedSymbols.size() >= 10) {
      String response =
          symbol
              + " cannot be subscribed to because the server is at its limit of 10 subscriptions";
      httpUtility.sendResponse(exchange, response, 409);
      return true;
    }
    return false;
  }
}

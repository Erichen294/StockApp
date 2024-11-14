package edu.bu.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.utils.HttpUtility;
import edu.bu.utils.VerifyStockSymbol;
import java.io.IOException;
import java.util.Set;

/** Handler for unsubscribe api via WebSocket callbacks from Finhub. */
public class UnsubscribeHandler implements HttpHandler {
  final StockUpdatesClient stockUpdatesClient;
  final VerifyStockSymbol verifyStockSymbol;
  public HttpUtility httpUtility;
  private final SymbolsPersistence symbolsPersistence;

  public UnsubscribeHandler(
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
    if (notSubscribedSendResponse(symbol, exchange)) {
      return;
    }

    // Unsubscribe from symbol and remove from persistence
    stockUpdatesClient.unsubscribeFromSymbol(symbol);
    stockUpdatesClient.removeSubscribedSymbolFromSet(symbol);
    symbolsPersistence.remove(symbol);

    // Respond with success
    String response = "StockApp is now unsubscribed from updates for " + symbol;
    httpUtility.sendResponse(exchange, response, 200);
  }

  /**
   * Internal helper that sends responses for symbols that are not subscribed
   *
   * @param symbol stock symbol of interest
   * @param exchange HttpExchange
   */
  public boolean notSubscribedSendResponse(String symbol, HttpExchange exchange)
      throws IOException {
    // Not subscribed
    Set<String> subscribedSymbols = stockUpdatesClient.getSubscribedSymbols();
    if (!subscribedSymbols.contains(symbol)) {
      String response = symbol + " has not been previously subscribed to";
      httpUtility.sendResponse(exchange, response, 409);
      return true;
    }
    return false;
  }
}

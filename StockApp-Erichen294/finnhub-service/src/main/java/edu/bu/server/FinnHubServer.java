package edu.bu.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.SubscribeHandler;
import edu.bu.server.handlers.SubscribedSymbolsHandler;
import edu.bu.server.handlers.UnsubscribeHandler;
import edu.bu.utils.VerifyStockSymbol;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/** Server for finnhub listener. */
public class FinnHubServer {
  private final StockUpdatesClient stockUpdatesClient;
  private final SymbolsPersistence symbolsPersistence;

  public FinnHubServer(
      StockUpdatesClient stockUpdatesClient, SymbolsPersistence symbolsPersistence) {
    this.stockUpdatesClient = stockUpdatesClient;
    this.symbolsPersistence = symbolsPersistence;
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8004), 0);

    // Create handler for subscribe api
    VerifyStockSymbol verifyStockSymbol = new VerifyStockSymbol();
    server.createContext(
        "/subscribe",
        new SubscribeHandler(stockUpdatesClient, verifyStockSymbol, symbolsPersistence));

    // Create handler for unsubscribe api
    server.createContext(
        "/unsubscribe",
        new UnsubscribeHandler(stockUpdatesClient, verifyStockSymbol, symbolsPersistence));

    // Create handler for subscribed-symbols api
    server.createContext("/subscribed-symbols", new SubscribedSymbolsHandler(stockUpdatesClient));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8004");
  }
}

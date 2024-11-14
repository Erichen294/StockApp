package edu.bu;

import edu.bu.finhub.FinHubWebSocketClient;
import edu.bu.finhub.MockFinhubClient;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.DatabaseSymbolPersistence;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.FinnHubServer;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.tinylog.Logger;

/** Entry point for FinnHubService. */
public class FinnHubService {
  public static final String MOCK_FINHUB_ARGUMENT = "mockFinhub";

  public static final String WEBHOOK_URI = "wss://ws.finnhub.io";
  public static final String API_TOKEN = "cq1vjm1r01ql95nces30cq1vjm1r01ql95nces3g";

  // FinnHubService
  public static void main(String[] args) throws IOException, URISyntaxException {
    Set<String> arguments = new HashSet<>(List.of(args));
    Logger.info("Starting StockAppServer with arguments: {}", List.of(args));

    // Creating EnqueueingFinnhubResponseHandler
    EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler =
        new EnqueueingFinnhubResponseHandler();

    // creating persistence
    SymbolsPersistence symbolsPersistence = new DatabaseSymbolPersistence();

    // register FinHub websocket listener, or mock based on argument to support local development
    StockUpdatesClient stockUpdatesClient =
        arguments.contains(MOCK_FINHUB_ARGUMENT)
            ? new MockFinhubClient(arguments, enqueueingFinnhubResponseHandler)
            : new FinHubWebSocketClient(
                WEBHOOK_URI + "?token=" + API_TOKEN,
                enqueueingFinnhubResponseHandler,
                symbolsPersistence);

    stockUpdatesClient.init();

    // Initialize subscribedSymbols using persistence
    stockUpdatesClient.initializeFromPersistence();

    // start server
    FinnHubServer finnHubServer = new FinnHubServer(stockUpdatesClient, symbolsPersistence);
    finnHubServer.start();
  }
}

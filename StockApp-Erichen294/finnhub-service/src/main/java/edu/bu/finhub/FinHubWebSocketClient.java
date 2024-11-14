package edu.bu.finhub;

import edu.bu.finhub.persistence.StoredSymbol;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.tinylog.Logger;

/**
 * Handles all interactions with the FinHub server. These include registration of stock symbols of
 * interest and handlers for FinHub responses.
 */
public class FinHubWebSocketClient extends WebSocketClient implements StockUpdatesClient {
  final Set<String> subscribedSymbols = new HashSet<>();
  private final EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler;
  private final SymbolsPersistence symbolsPersistence;

  public FinHubWebSocketClient(
      String serverUri,
      EnqueueingFinnhubResponseHandler enqueueingFinnhubResponseHandler,
      SymbolsPersistence symbolsPersistence)
      throws URISyntaxException {
    super(new URI(serverUri));
    this.enqueueingFinnhubResponseHandler = enqueueingFinnhubResponseHandler;
    this.symbolsPersistence = symbolsPersistence;
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    Logger.info("WebSocket connection established");
  }

  @Override
  public Set<String> getSubscribedSymbols() {
    return Collections.unmodifiableSet(subscribedSymbols);
  }

  @Override
  public void addSubscribedSymbolToSet(String symbol) {
    subscribedSymbols.add(symbol);
  }

  @Override
  public void removeSubscribedSymbolFromSet(String symbol) {
    subscribedSymbols.remove(symbol);
  }

  @Override
  public void subscribeToSymbol(String symbol) {
    String message = "{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public void unsubscribeFromSymbol(String symbol) {
    String message = "{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}";
    send(message);
  }

  @Override
  public void initializeFromPersistence() {
    Set<StoredSymbol> storedSymbols = symbolsPersistence.readAll();
    Instant tenDaysAgo = Instant.now().minus(Duration.ofDays(10));

    for (StoredSymbol symbol : storedSymbols) {
      // Only add symbols to set and subscribe if it is created within the last 10 days
      if (symbol.createdAt.isAfter(tenDaysAgo)) {
        subscribedSymbols.add(symbol.symbol);
        subscribeToSymbol(symbol.symbol);
      }
    }
  }

  @Override
  public void onMessage(String message) {
    Logger.info("Received FinHub message {}", message);

    try {
      enqueueingFinnhubResponseHandler.enqueueResponseToQueue(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    Logger.warn("Closing FinHub client due to {}", reason);
  }

  @Override
  public void onError(Exception exception) {
    Logger.warn("Received FinHub error: {}", exception.getMessage());
  }

  @Override
  public void init() {
    Logger.info("Starting WebSocket based FinHub client");
    try {
      super.connectBlocking();
      Logger.info("WebSocket connection established successfully");
    } catch (InterruptedException e) {
      Logger.error("WebSocket connection was interrupted: {}", e.getMessage());
    }
  }
}

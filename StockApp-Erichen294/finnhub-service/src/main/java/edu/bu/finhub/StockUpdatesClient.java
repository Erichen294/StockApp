package edu.bu.finhub;

import java.util.Set;

/**
 * Implementors provide stock updates, usually by communicating with an external data provider like
 * FinHub or by providing canned responses for testing purposes.
 */
public interface StockUpdatesClient {
  /** Connect to the server that will be providing this client with updates. */
  void init();

  Set<String> getSubscribedSymbols();

  void addSubscribedSymbolToSet(String symbol);

  void removeSubscribedSymbolFromSet(String symbol);

  void subscribeToSymbol(String symbol);

  void unsubscribeFromSymbol(String symbol);

  void initializeFromPersistence();
}

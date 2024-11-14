package edu.bu.data;

import edu.bu.finhub.FinhubResponse;
import java.util.List;
import java.util.Set;

/**
 * Implementors store data that is received from FinHub and make it available for StockApp internal
 * computations
 */
public interface DataStore {
  /** Handle an update received from FinHub. */
  void update(List<FinhubResponse> responses);

  /** Get the entire history of updates that we have seen for Stock symbol. */
  List<FinhubResponse> getHistory(String symbol);

  /** Retrieve the set of all symbols that this store has seen. */
  Set<String> knownSymbols();

  /** Return true if the store has seen the given symbol. */
  boolean haveSymbol(String symbol);

  /**
   * @return the nummber of total updates, across all stocks seen by this data store.
   */
  int numberOfUpdatesSeen();
}

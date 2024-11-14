package edu.bu.data;

import edu.bu.finhub.FinhubResponse;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A data store implementation that keeps all data in memory only. This implies that data is not
 * persisted across server restarts.
 */
public class InMemoryStore implements DataStore {
  final Map<String, Stack<FinhubResponse>> responsesBySymbol;

  // thread safe integer ensures that processes can be calling update and incrementing this count
  // while others are concurrently calling numberOfUpdatesSeen and this will behave as expected
  final AtomicInteger numberOfUpdates = new AtomicInteger(0);

  public InMemoryStore() {
    this.responsesBySymbol = new ConcurrentHashMap<>();
  }

  @Override
  public void update(List<FinhubResponse> responses) {
    for (FinhubResponse response : responses) {
      responsesBySymbol
          .computeIfAbsent(response.symbol.toUpperCase(), key -> new Stack<>())
          .push(response);
    }
    numberOfUpdates.incrementAndGet();
  }

  @Override
  public List<FinhubResponse> getHistory(String symbol) {
    // by default when a list is created from a stack, it reverses the order, we want to preserve
    // stack order here
    List<FinhubResponse> history = new ArrayList<>(responsesBySymbol.get(symbol.toUpperCase()));
    Collections.reverse(history);

    return history;
  }

  @Override
  public Set<String> knownSymbols() {
    return new HashSet<>(responsesBySymbol.keySet());
  }

  @Override
  public boolean haveSymbol(String symbol) {
    return responsesBySymbol.containsKey(symbol.toUpperCase());
  }

  @Override
  public int numberOfUpdatesSeen() {
    return numberOfUpdates.get();
  }
}

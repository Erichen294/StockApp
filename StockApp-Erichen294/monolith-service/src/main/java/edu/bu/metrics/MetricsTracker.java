package edu.bu.metrics;

import edu.bu.finhub.FinhubResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Metrics tracker for tracking updates and other metrics related. */
public class MetricsTracker {
  final Map<String, Integer> priceRequestCount = new HashMap<>();
  final Map<String, Integer> updatesCount = new HashMap<>();

  public String setUpdatesCount(Set<String> knownSymbols, List<List<FinhubResponse>> historyList) {
    // Clear out updatesCount
    updatesCount.clear();

    // Populate updatesCount based on updated seen symbols
    int index = 0;
    for (String symbol : knownSymbols) {
      updatesCount.put(symbol, historyList.get(index).size());
      index += 1;
    }
    return getUpdatesVolume();
  }

  public String getUpdatesVolume() {
    return "Updates-volume:\n"
        + updatesCount.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // Sort by value
            .map(e -> e.getKey() + " : " + e.getValue()) // Format each line
            .collect(Collectors.joining("\n"));
  }

  public void incrementPriceRequestCount(String symbol) {
    priceRequestCount.put(symbol, priceRequestCount.getOrDefault(symbol, 0) + 1);
  }

  public String getPriceVolume() {
    StringBuilder result = new StringBuilder("Price-volume:\n");

    // Sort symbols by their request count in descending order and build the output string
    priceRequestCount.entrySet().stream()
        .sorted(
            (e1, e2) ->
                e2.getValue()
                    .compareTo(e1.getValue())) // Sort by value (request count) in descending order
        .forEach(
            entry ->
                result.append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n"));

    return result.toString();
  }
}

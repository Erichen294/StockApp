package edu.bu.metrics.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubResponse;
import edu.bu.metrics.MetricsTracker;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** Handler for updates-volume api. */
public class UpdatesVolumeHandler implements HttpHandler {
  final DataStore store;
  final MetricsTracker metricsTracker;
  final HttpUtility httpUtility = new HttpUtility();

  public UpdatesVolumeHandler(DataStore store, MetricsTracker metricsTracker) {
    this.store = store;
    this.metricsTracker = metricsTracker;
  }

  // Returns the number of updates we have seen for each symbol sorted in descending order
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    Set<String> knownSymbols = store.knownSymbols();
    List<List<FinhubResponse>> historyList = new ArrayList<>();
    // Get history of all subscribed symbols
    for (String symbol : knownSymbols) {
      List<FinhubResponse> history = store.getHistory(symbol);
      historyList.add(history);
    }

    String response = metricsTracker.setUpdatesCount(knownSymbols, historyList);
    // Send the response
    httpUtility.sendResponse(exchange, response, 200);
  }
}

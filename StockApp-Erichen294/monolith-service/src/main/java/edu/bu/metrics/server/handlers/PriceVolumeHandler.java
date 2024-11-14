package edu.bu.metrics.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.metrics.MetricsTracker;
import edu.bu.utils.HttpUtility;
import java.io.IOException;

/** Handler for price-volume api. */
public class PriceVolumeHandler implements HttpHandler {
  final MetricsTracker metricsTracker;
  final HttpUtility httpUtility = new HttpUtility();

  public PriceVolumeHandler(MetricsTracker metricsTracker) {
    this.metricsTracker = metricsTracker;
  }

  // Returns the number of price requests seen for each symbol sorted in descending order
  @Override
  public void handle(HttpExchange exchange) throws IOException {
    String response = metricsTracker.getPriceVolume();
    // Send the response
    httpUtility.sendResponse(exchange, response, 200);
  }
}

package edu.bu.metrics.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.data.DataStore;
import edu.bu.metrics.MetricsTracker;
import edu.bu.metrics.server.handlers.PriceVolumeHandler;
import edu.bu.metrics.server.handlers.UpdatesVolumeHandler;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/**
 * Registers RESTful HTTP endpoints that are supported by the StockApp. These endpoints will be
 * invoked to retrieve metrics.
 */
public class MetricsWebServer {
  final MetricsTracker metricsTracker;
  final DataStore store;

  public MetricsWebServer(MetricsTracker metricsTracker, DataStore store) {
    this.metricsTracker = metricsTracker;
    this.store = store;
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);

    // Create handler for updates-volume api
    server.createContext("/updates-volume", new UpdatesVolumeHandler(store, metricsTracker));

    // Create handler for price-volume api
    server.createContext("/price-volume", new PriceVolumeHandler(metricsTracker));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8001");
  }
}

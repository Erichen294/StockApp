package edu.bu;

import edu.bu.analytics.AnalyticsComputor;
import edu.bu.analytics.BasicAnalyticsComputor;
import edu.bu.analytics.CachingAnalyticsComputor;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.metrics.MetricsTracker;
import edu.bu.metrics.server.MetricsWebServer;
import edu.bu.server.BasicWebServer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.tinylog.Logger;

/**
 * Entry point for the entire single process StockApp. It initializes the following collaborators:
 * <li>FinHubClient - to register listeners for WebSocket updates from FinHub
 * <li>BasicWebServer - to configure supported HTTP endpoints for our (StockApp) users
 * <li>DataStore - an instance of a data store to store data that we receive from FinHub
 * <li>BasicAnalyticsComputor - business logic around all computations performed by our app
 */
public class StockAppServer {
  // StockAppServer
  public static void main(String[] args) throws IOException, URISyntaxException {
    Logger.info("Starting StockAppServer with arguments: {}", List.of(args));

    // set up store
    DataStore store = new InMemoryStore();

    // set up queuereader and start reading from queue
    QueueReader queueReader = new QueueReader(store);
    queueReader.readQueueService();

    // set up analytics computations
    AnalyticsComputor analyticsComputor =
        new CachingAnalyticsComputor(new BasicAnalyticsComputor(store), store);

    // Create instance of MetricsTracker
    MetricsTracker metricsTracker = new MetricsTracker();

    // start web server
    BasicWebServer webServer = new BasicWebServer(store, analyticsComputor, metricsTracker);
    webServer.start();

    // Create instance of MetricsWebServer
    MetricsWebServer metricsWebServer = new MetricsWebServer(metricsTracker, store);
    metricsWebServer.start();
  }
}

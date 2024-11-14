package edu.bu.server;

import com.sun.net.httpserver.HttpServer;
import edu.bu.handlers.DequeueHandler;
import edu.bu.handlers.EnqueueHandler;
import edu.bu.queue.InMemoryQueue;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.tinylog.Logger;

/** In-memory queue service with a RESTful web interface */
public class QueueServer {
  private final InMemoryQueue inMemoryQueue;

  public QueueServer() {
    inMemoryQueue = new InMemoryQueue();
  }

  public void start() throws IOException {
    // Create an HttpServer instance
    HttpServer server = HttpServer.create(new InetSocketAddress(8010), 0);

    // Create handler for enqueue endpoint
    server.createContext("/enqueue", new EnqueueHandler(inMemoryQueue));

    // Create handler for dequeue endpoint
    server.createContext("/dequeue", new DequeueHandler(inMemoryQueue));

    // Start the server
    server.setExecutor(null); // Use the default executor
    server.start();

    Logger.info("Server is running on port 8010");
  }
}

package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.InMemoryQueue;
import edu.bu.utils.HttpUtility;
import java.io.IOException;

/** Handler for dequeue endpoint */
public class DequeueHandler implements HttpHandler {
  private final InMemoryQueue inMemoryQueue;
  private final HttpUtility httpUtility;

  public DequeueHandler(InMemoryQueue inMemoryQueue) {
    this.inMemoryQueue = inMemoryQueue;
    httpUtility = new HttpUtility();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // Tries to get next message from queue
    String message = inMemoryQueue.dequeue();
    if (message != null) {
      httpUtility.sendResponse(exchange, message, 200);
    } else {
      httpUtility.sendResponse(exchange, "", 204);
    }
  }
}

package edu.bu.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import edu.bu.queue.InMemoryQueue;
import edu.bu.utils.HttpUtility;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/** Handler for enqueue endpoint */
public class EnqueueHandler implements HttpHandler {
  private final InMemoryQueue inMemoryQueue;
  private final HttpUtility httpUtility;

  public EnqueueHandler(InMemoryQueue inMemoryQueue) {
    this.inMemoryQueue = inMemoryQueue;
    httpUtility = new HttpUtility();
  }

  @Override
  public void handle(HttpExchange exchange) throws IOException {
    // Check if the request method is PUT
    if ("PUT".equalsIgnoreCase(exchange.getRequestMethod())) {
      // Read the request body
      String requestBody =
          new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
      inMemoryQueue.enqueue(requestBody);
      httpUtility.sendResponse(exchange, "Message enqueued", 200);
    }
  }
}

package edu.bu;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubParser;
import edu.bu.finhub.FinhubResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.tinylog.Logger;

/** Reads events from the queue-service. */
public class QueueReader {
  private final DataStore datastore;
  private final HttpClient httpClient;
  private final FinhubParser parser;

  public QueueReader(DataStore dataStore) {
    this.datastore = dataStore;
    this.httpClient = HttpClient.newHttpClient();
    this.parser = new FinhubParser();
  }

  public void readQueueService() {
    new Thread(
            () -> {
              while (true) {
                try {
                  HttpRequest request =
                      HttpRequest.newBuilder()
                          .uri(new URI("http://localhost:8010/dequeue"))
                          .GET()
                          .build();
                  HttpResponse<String> response =
                      httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                  // Handle HTTP response
                  if (response.statusCode() == 200) {
                    // List<FinhubResponse> finhubResponses = parseResponses(response.body());
                    List<FinhubResponse> finhubResponses = parser.parse(response.body());
                    Logger.info("Read from queue: {}", response.body());
                    datastore.update(finhubResponses);
                  } else if (response.statusCode() == 204) {
                    // No content, queue is empty
                    Logger.info("Queue is empty.");
                  } else {
                    Logger.info("Unexpected response code.");
                  }
                } catch (IOException | InterruptedException | URISyntaxException e) {
                  System.err.println("Error while reading from the queue: " + e.getMessage());
                }
                try {
                  Thread.sleep(500); // Sleep for half a second
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt(); // Restore interrupt status
                }
              }
            })
        .start();
  }
}

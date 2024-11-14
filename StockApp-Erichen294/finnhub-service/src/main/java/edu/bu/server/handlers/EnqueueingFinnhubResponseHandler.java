package edu.bu.server.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.tinylog.Logger;

/**
 * Handler that receives Finnhub JSON responses and enqueues that string into queue-service by
 * making HTTP PUT request.
 */
public class EnqueueingFinnhubResponseHandler {
  // Tries to add string into queue-service and returns the response code
  public int enqueueResponseToQueue(String finnhubJsonResponse) throws IOException {
    if (finnhubJsonResponse.contains("\"type\":\"ping\"")) {
      Logger.info("Ping message ignored.");
      return 204;
    } else {
      URL url = new URL("http://localhost:8010/enqueue");
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("PUT");
      connection.setRequestProperty("Content-Type", "application/json");
      connection.setDoOutput(true);

      try (OutputStream outputStream = connection.getOutputStream()) {
        byte[] input = finnhubJsonResponse.getBytes("utf-8");
        outputStream.write(input, 0, input.length);
      }
      return connection.getResponseCode();
    }
  }
}

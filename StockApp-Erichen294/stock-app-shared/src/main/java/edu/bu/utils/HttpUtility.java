package edu.bu.utils;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;

/** Class to send http response messages. */
public class HttpUtility {
  // Sends http response
  public void sendResponse(HttpExchange exchange, String response, int code) throws IOException {
    exchange.sendResponseHeaders(code, response.length());
    OutputStream outputStream = null;
    try {
      outputStream = exchange.getResponseBody();
      outputStream.write(response.getBytes());
    } finally {
      if (outputStream != null) {
        outputStream.close();
      }
    }
  }
}

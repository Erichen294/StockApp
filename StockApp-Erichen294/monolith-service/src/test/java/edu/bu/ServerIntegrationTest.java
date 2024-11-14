package edu.bu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ServerIntegrationTest {

  HttpClient httpClient;

  @BeforeEach
  public void setUp() {
    httpClient = HttpClient.newBuilder().build();
  }

  @Test
  public void requestAgainstRunningServer_knownSymbols()
      throws URISyntaxException, IOException, InterruptedException, ParseException {

    // Make the request
    String rawResponse = makeRequest("http://localhost:8000/symbols");

    // Parse the JSON response to a Set<String>
    JSONParser parser = new JSONParser();
    JSONArray jsonResponse = (JSONArray) parser.parse(rawResponse);

    Set<String> actualSymbols = new HashSet<>(jsonResponse);

    // Expected symbols
    Set<String> expectedSymbols = Set.of("NKE", "BAC", "RIVN");

    // Assert equality
    assertEquals(expectedSymbols, actualSymbols);
  }

  @Test
  public void requestAgainstRunningServer_price()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8000/price/BAC");

    assertEquals(rawResponse, "{\"symbol\":\"BAC\",\"currentPrice\":43.1}");
  }

  @Test
  public void requestAgainstRunningServer_mostActiveStock()
      throws URISyntaxException, IOException, InterruptedException {

    String rawResponse = makeRequest("http://localhost:8000/mostactive/");

    assertEquals(rawResponse, "{\"mostActiveStock\":\"RIVN\"}");
  }

  @Test
  public void requestAgainstRunningServer_averageVolume()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8000/averagevolume/BAC");

    assertEquals("{\"symbol\":\"BAC\",\"averageVolumePerSecond\":4.75}", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_subscribe_validSymbol()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8004/subscribe/NVDA");

    assertEquals("StockApp is now subscribed to updates for NVDA", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_subscribe_alreadySubscribed()
      throws URISyntaxException, IOException, InterruptedException {
    // First subscribe to a symbol
    makeRequest("http://localhost:8004/subscribe/TSLA");

    // Try subscribing to the same symbol again
    String rawResponse = makeRequest("http://localhost:8004/subscribe/TSLA");

    assertEquals("TSLA has already been registered", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_subscribe_exceedLimit()
      throws URISyntaxException, IOException, InterruptedException {
    // Subscribe to 10 symbols
    for (String symbol :
        List.of("AAPL", "GOOGL", "AMZN", "MSFT", "META", "NFLX", "TSLA", "BAC", "NKE", "RIVN")) {
      makeRequest("http://localhost:8004/subscribe/" + symbol);
    }

    // Now try subscribing to one more symbol (limit exceeded)
    String rawResponse = makeRequest("http://localhost:8004/subscribe/ABCD");

    assertEquals(
        "ABCD cannot be subscribed to because the server is at its limit of 10 subscriptions",
        rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_subscribe_invalidSymbol()
      throws URISyntaxException, IOException, InterruptedException {
    // Try subscribing to an invalid symbol (with special characters or too long)
    String rawResponse = makeRequest("http://localhost:8004/subscribe/INVALID@");

    assertEquals("INVALID@ is not a valid US Stock symbol", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_unsubscribe_invalidSymbol()
      throws URISyntaxException, IOException, InterruptedException {
    // Try subscribing to an invalid symbol (with special characters or too long)
    String rawResponse = makeRequest("http://localhost:8004/unsubscribe/INVALID@");

    assertEquals("INVALID@ is not a valid US Stock symbol", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_unsubscribe_validSymbol()
      throws URISyntaxException, IOException, InterruptedException {
    makeRequest("http://localhost:8004/subscribe/NVDA");
    String rawResponse = makeRequest("http://localhost:8004/unsubscribe/NVDA");
    assertEquals("StockApp is now unsubscribed from updates for NVDA", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_unsubscribe_symbolDoesNotExist()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8004/unsubscribe/ABCD");
    assertEquals("ABCD has not been previously subscribed to", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_subscribedSymbols_hasSubscriptions()
      throws URISyntaxException, IOException, InterruptedException {
    makeRequest("http://localhost:8004/subscribe/TSLA");
    String rawResponse = makeRequest("http://localhost:8004/subscribed-symbols");

    assertEquals(
        "StockApp is subscribed to 4 symbols. They are: BAC, NKE, RIVN, TSLA.", rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_updatesVolume_hasSubscriptions()
      throws URISyntaxException, IOException, InterruptedException {
    String rawResponse = makeRequest("http://localhost:8001/updates-volume");

    String expectedResponse = "Updates-volume:\nBAC : 5\nRIVN : 5\nNKE : 4";
    assertEquals(expectedResponse, rawResponse);
  }

  @Test
  public void requestAgainstRunningServer_priceVolume_multiplePriceRequests()
      throws URISyntaxException, IOException, InterruptedException {
    // Making AAPL price requests and MSFT price requests
    makeRequest("http://localhost:8000/price/AAPL");
    makeRequest("http://localhost:8000/price/AAPL");
    makeRequest("http://localhost:8000/price/AAPL");
    makeRequest("http://localhost:8000/price/MSFT");
    makeRequest("http://localhost:8000/price/MSFT");

    String rawResponse = makeRequest("http://localhost:8001/price-volume");
    String expectedResponse = "Price-volume:\nAAPL : 3\nMSFT : 2";
    assertEquals(expectedResponse, rawResponse);
  }

  String makeRequest(String url) throws URISyntaxException, IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    return response.body().strip();
  }
}

package edu.bu.finhub;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import edu.bu.finhub.persistence.StoredSymbol;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.EnqueueingFinnhubResponseHandler;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FinHubWebSocketClientTest {

  private FinHubWebSocketClient finHubWebSocketClient;
  private EnqueueingFinnhubResponseHandler mockEnqueueingHandler;
  private SymbolsPersistence mockSymbolsPersistence;

  @BeforeEach
  public void setUp() throws URISyntaxException {
    // Mock the EnqueueingFinnhubResponseHandler
    mockEnqueueingHandler = mock(EnqueueingFinnhubResponseHandler.class);

    // Mock the SymbolsPersistence
    mockSymbolsPersistence = mock(SymbolsPersistence.class);

    // Pass the mocked handler to FinHubWebSocketClient
    finHubWebSocketClient =
        spy(
            new FinHubWebSocketClient(
                "wss://test.finHub.com", mockEnqueueingHandler, mockSymbolsPersistence));

    // Stub the send method to avoid actual WebSocket communication
    doNothing().when(finHubWebSocketClient).send(anyString());
  }

  @Test
  public void testSubscribeToSymbol() {
    String symbol = "AAPL";
    String expectedMessage = "{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}";

    // Test subscribing to a symbol
    finHubWebSocketClient.subscribeToSymbol(symbol);

    // Verify that the expected WebSocket message was sent
    verify(finHubWebSocketClient).send(expectedMessage);
  }

  @Test
  public void testUnsubscribeFromSymbol() {
    String symbol = "AAPL";
    String expectedMessage = "{\"type\":\"unsubscribe\",\"symbol\":\"" + symbol + "\"}";

    // Test unsubscribing from a symbol
    finHubWebSocketClient.unsubscribeFromSymbol(symbol);

    // Verify that the expected WebSocket message was sent
    verify(finHubWebSocketClient).send(expectedMessage);
  }

  @Test
  public void testInitializeFromPersistence() {
    // Create mock symbols
    Instant now = Instant.now();
    Instant recentDate = now.minusSeconds(5 * 24 * 60 * 60); // 5 days ago
    Instant oldDate = now.minusSeconds(15 * 24 * 60 * 60); // 15 days ago

    StoredSymbol recentSymbol = new StoredSymbol("AAPL", recentDate);
    StoredSymbol oldSymbol = new StoredSymbol("TSLA", oldDate);

    // Mock the readAll method to return both recent and old symbols
    when(mockSymbolsPersistence.readAll()).thenReturn(Set.of(recentSymbol, oldSymbol));

    // Call initializeFromPersistence
    finHubWebSocketClient.initializeFromPersistence();

    // Verify that only the recent symbol was added
    assertTrue(finHubWebSocketClient.getSubscribedSymbols().contains("AAPL"));
    assertFalse(finHubWebSocketClient.getSubscribedSymbols().contains("TSLA"));
  }
}

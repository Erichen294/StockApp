package edu.bu.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.UnsubscribeHandler;
import edu.bu.utils.HttpUtility;
import edu.bu.utils.VerifyStockSymbol;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UnsubscribeHandlerTest {

  private UnsubscribeHandler unsubscribeHandler;
  private StockUpdatesClient stockUpdatesClient;
  private VerifyStockSymbol verifyStockSymbol;
  private HttpExchange exchange;
  private HttpUtility httpUtility; // Declare httpUtility
  private SymbolsPersistence symbolsPersistence;

  @BeforeEach
  public void setUp() {
    stockUpdatesClient = mock(StockUpdatesClient.class);
    verifyStockSymbol = mock(VerifyStockSymbol.class);
    httpUtility = mock(HttpUtility.class); // Mock HttpUtility
    symbolsPersistence = mock(SymbolsPersistence.class);

    unsubscribeHandler =
        new UnsubscribeHandler(stockUpdatesClient, verifyStockSymbol, symbolsPersistence);
    unsubscribeHandler.httpUtility = httpUtility; // Override in handler
    exchange = mock(HttpExchange.class);
  }

  @Test
  public void testNotSubscribedSendResponse_ReturnsTrue_WhenSymbolNotSubscribed()
      throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    assertTrue(unsubscribeHandler.notSubscribedSendResponse(symbol, exchange));
  }

  @Test
  public void testNotSubscribedSendResponse_ReturnsFalse_WhenSymbolSubscribed() throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    subscribedSymbols.add(symbol);
    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    assertFalse(unsubscribeHandler.notSubscribedSendResponse(symbol, exchange));
  }
}

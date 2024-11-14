package edu.bu.handlers;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sun.net.httpserver.HttpExchange;
import edu.bu.finhub.StockUpdatesClient;
import edu.bu.finhub.persistence.SymbolsPersistence;
import edu.bu.server.handlers.SubscribeHandler;
import edu.bu.utils.HttpUtility;
import edu.bu.utils.VerifyStockSymbol;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubscribeHandlerTest {

  private StockUpdatesClient stockUpdatesClient;
  private VerifyStockSymbol verifyStockSymbol;
  private HttpUtility httpUtility;
  private SubscribeHandler subscribeHandler;
  private HttpExchange exchange;
  private SymbolsPersistence symbolsPersistence;

  @BeforeEach
  public void setUp() throws IOException, URISyntaxException {
    stockUpdatesClient = mock(StockUpdatesClient.class);
    verifyStockSymbol = mock(VerifyStockSymbol.class);
    httpUtility = mock(HttpUtility.class);
    symbolsPersistence = mock(SymbolsPersistence.class);

    subscribeHandler =
        new SubscribeHandler(stockUpdatesClient, verifyStockSymbol, symbolsPersistence);

    exchange = mock(HttpExchange.class);
    when(exchange.getRequestURI()).thenReturn(new URI("/subscribe/AAPL"));
    when(exchange.getResponseBody()).thenReturn(new ByteArrayOutputStream());
  }

  @Test
  public void testAlreadySubscribedSendResponse_AlreadySubscribed() throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    subscribedSymbols.add(symbol);

    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    boolean result = subscribeHandler.alreadySubscribedSendResponse(symbol, exchange);

    assertTrue(result);
  }

  @Test
  public void testAlreadySubscribedSendResponse_NotSubscribed() throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();

    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    boolean result = subscribeHandler.alreadySubscribedSendResponse(symbol, exchange);

    assertFalse(result);
  }

  @Test
  public void testAboveSubscribedLimitSendResponse_AboveLimit() throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    for (int i = 0; i < 10; i++) {
      subscribedSymbols.add("SYMBOL" + i);
    }

    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    boolean result = subscribeHandler.aboveSubscribedLimitSendResponse(symbol, exchange);

    assertTrue(result);
  }

  @Test
  public void testAboveSubscribedLimitSendResponse_BelowLimit() throws IOException {
    String symbol = "AAPL";
    Set<String> subscribedSymbols = new HashSet<>();
    for (int i = 0; i < 9; i++) {
      subscribedSymbols.add("SYMBOL" + i);
    }

    when(stockUpdatesClient.getSubscribedSymbols()).thenReturn(subscribedSymbols);

    boolean result = subscribeHandler.aboveSubscribedLimitSendResponse(symbol, exchange);

    assertFalse(result);
  }
}

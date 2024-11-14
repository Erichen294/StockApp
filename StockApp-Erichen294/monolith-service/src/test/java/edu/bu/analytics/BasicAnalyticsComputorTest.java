package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicAnalyticsComputorTest {

  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);

  AnalyticsComputor analyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    analyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void currentPrice_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(UnknownSymbolException.class, () -> analyticsComputor.currentPrice("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void currentPrice_singleValue() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(134.12, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void currentPrice_multipleValues() throws UnknownSymbolException {
    // first response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // second response 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    // third response 13 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals(135.01, analyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> analyticsComputor.totalObservedVolume("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100, analyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    // Add first data point for "NVDA"
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // Add second data point for "NVDA" with new volume 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.45, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    // Add first data point for "AAPL"
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 142.50, TEST_TIME.plus(10, ChronoUnit.SECONDS).toEpochMilli(), 150)));

    assertEquals(300, analyticsComputor.totalObservedVolume("NVDA"));

    assertEquals(150, analyticsComputor.totalObservedVolume("AAPL"));
  }

  @Test
  public void averageVolumePerSecond_invalidSymbolAlphaNumeric()
      throws InvalidSymbolException, UnknownSymbolException {
    InvalidSymbolException exception =
        assertThrows(
            InvalidSymbolException.class, () -> analyticsComputor.averageVolumePerSecond("ABCDEF"));
    assertEquals("ABCDEF is not a valid symbol", exception.getMessage());
  }

  @Test
  public void averageVolumePerSecond_invalidSymbolSpecialCharacter()
      throws InvalidSymbolException, UnknownSymbolException {
    InvalidSymbolException exception =
        assertThrows(
            InvalidSymbolException.class, () -> analyticsComputor.averageVolumePerSecond("$$^AB"));
    assertEquals("$$^AB is not a valid symbol", exception.getMessage());
  }

  @Test
  public void averageVolumePerSecond_invalidSymbolNull()
      throws InvalidSymbolException, UnknownSymbolException {
    InvalidSymbolException exception =
        assertThrows(
            InvalidSymbolException.class, () -> analyticsComputor.averageVolumePerSecond(null));
    assertEquals("null is not a valid symbol", exception.getMessage());
  }

  @Test
  public void averageVolumePerSecond_invalidSymbolEmpty()
      throws InvalidSymbolException, UnknownSymbolException {
    InvalidSymbolException exception =
        assertThrows(
            InvalidSymbolException.class, () -> analyticsComputor.averageVolumePerSecond(""));
    assertEquals(" is not a valid symbol", exception.getMessage());
  }

  @Test
  public void averageVolumePerSecond_unknownSymbol()
      throws InvalidSymbolException, UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> analyticsComputor.averageVolumePerSecond("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void averageVolumePerSecond_singleDataPoint()
      throws InvalidSymbolException, UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100.0, analyticsComputor.averageVolumePerSecond("NVDA"));
  }

  @Test
  public void averageVolumePerSecond_multipleMixedDataPoint()
      throws InvalidSymbolException, UnknownSymbolException {
    // Add first data point for "NVDA"
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    // Add second data point for "NVDA" with new volume 5 seconds later
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 135.45, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    // Add first data point for "AAPL"
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 142.50, TEST_TIME.plus(10, ChronoUnit.SECONDS).toEpochMilli(), 150)));

    assertEquals(60.0, analyticsComputor.averageVolumePerSecond("NVDA"));

    assertEquals(150.0, analyticsComputor.averageVolumePerSecond("AAPL"));
  }

  @Test
  public void mostActiveStock_noData() {
    assertNull(analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    // Add single response to dataStore
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 135.00, TEST_TIME.toEpochMilli(), 150));
    dataStore.update(singleResponse);

    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    // Add single response to dataStore (highest volume)
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 500)));

    // Add multiple responses to dataStore (less volume than single response)
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 140.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 142.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 142.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 142.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    // Adding multiple data points (highest volume)
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 136.00, TEST_TIME.toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 400)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 400)));

    // Adding single data points for other symbols
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 134.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "MSFT", 134.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    assertEquals("NVDA", analyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStockInWindow_noDataInWindow() {
    // Update dataStore with known symbols but no data points in the time window
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse("NVDA", 135.00, TEST_TIME.minusSeconds(10).toEpochMilli(), 100),
            new FinhubResponse("AAPL", 134.00, TEST_TIME.minusSeconds(10).toEpochMilli(), 50)));

    Instant start = TEST_TIME; // current time
    Instant end = start.plusSeconds(5); // 5 seconds later

    assertNull(analyticsComputor.mostActiveStock(start, end));
  }

  @Test
  public void mostActiveStockInWindow_singleDataPoint() {
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 135.69, TEST_TIME.toEpochMilli(), 100)));

    Instant start = TEST_TIME;
    Instant end = start.plusSeconds(1);

    assertEquals("NVDA", analyticsComputor.mostActiveStock(start, end));
  }

  @Test
  public void mostActiveStockInWindow_largestVolumeAlsoMostDataPoints() {
    // Adding multiple data points (highest volume)
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 136.00, TEST_TIME.toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 200)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 400)));

    // Adding single data points for other symbols
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 134.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "MSFT", 134.72, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100)));

    Instant start = TEST_TIME;
    Instant end = start.plusSeconds(5);

    assertEquals("NVDA", analyticsComputor.mostActiveStock(start, end));
  }

  @Test
  public void mostActiveStockInWindow_DataPointsOutsideWindow() {
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.minus(10, ChronoUnit.SECONDS).toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.minus(5, ChronoUnit.SECONDS).toEpochMilli(), 200)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 136.00, TEST_TIME.plus(10, ChronoUnit.SECONDS).toEpochMilli(), 400)));

    Instant start = TEST_TIME;
    Instant end = start.plusSeconds(5);

    assertNull(analyticsComputor.mostActiveStock(start, end));
  }

  @Test
  public void knownSymbols_empty() {
    assertTrue(analyticsComputor.knownSymbols().isEmpty());
  }

  @Test
  public void knownSymbols_single() {
    // Adding one response
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 135.69, TEST_TIME.toEpochMilli(), 100)));

    Set<String> symbols = analyticsComputor.knownSymbols();

    assertEquals(1, symbols.size());
    assertTrue(symbols.contains("NVDA"));
  }

  @Test
  public void knownSymbols_multiple() {
    // Adding multiple symbols into dataStore
    dataStore.update(
        ImmutableList.of(new FinhubResponse("NVDA", 136.92, TEST_TIME.toEpochMilli(), 100)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "AAPL", 172.21, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 150)));
    dataStore.update(
        ImmutableList.of(
            new FinhubResponse(
                "MSFT", 430.53, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 200)));

    Set<String> symbols = analyticsComputor.knownSymbols();

    assertEquals(3, symbols.size());
    assertTrue(symbols.contains("NVDA"));
    assertTrue(symbols.contains("AAPL"));
    assertTrue(symbols.contains("MSFT"));
  }
}

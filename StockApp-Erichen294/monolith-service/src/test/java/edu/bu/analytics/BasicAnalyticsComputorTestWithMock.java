package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BasicAnalyticsComputorTestWithMock {

  static final Instant TEST_TIME = Instant.now();
  AnalyticsComputor basicAnalyticsComputor;
  DataStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = mock(DataStore.class);
    basicAnalyticsComputor = new BasicAnalyticsComputor(dataStore);
  }

  @Test
  public void currentPrice_unknownSymbol() throws UnknownSymbolException {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> basicAnalyticsComputor.currentPrice("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void currentPrice_singleValue() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(134.12, basicAnalyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void currentPrice_multipleValues() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);

    // recall that getHistory returns responses in Stack order - Last In First Out
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 135.01, TEST_TIME.plus(13, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse(
                    "NVDA", 135.33, TEST_TIME.plus(5, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100)));

    assertEquals(135.01, basicAnalyticsComputor.currentPrice("NVDA"), 0.01);
  }

  @Test
  public void totalObservedVolume_unknownSymbol() {
    UnknownSymbolException exception =
        assertThrows(
            UnknownSymbolException.class, () -> basicAnalyticsComputor.totalObservedVolume("NVDA"));
    assertEquals("NVDA has not been seen by the server", exception.getMessage());
  }

  @Test
  public void totalObservedVolume_oneDataPoint() throws UnknownSymbolException {
    // Add a single data point
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 135.68, TEST_TIME.toEpochMilli(), 200)));

    assertEquals(200, basicAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_multipleMixedDataPoints() throws UnknownSymbolException {
    when(dataStore.haveSymbol("NVDA")).thenReturn(true);
    when(dataStore.haveSymbol("AAPL")).thenReturn(true);

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 137.92, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 250),
                new FinhubResponse(
                    "NVDA", 135.90, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 100),
                new FinhubResponse("NVDA", 134.18, TEST_TIME.toEpochMilli(), 100)));

    when(dataStore.getHistory("AAPL"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "AAPL", 134.92, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 50),
                new FinhubResponse(
                    "AAPL", 135.90, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 50),
                new FinhubResponse("AAPL", 134.18, TEST_TIME.toEpochMilli(), 50)));

    assertEquals(450, basicAnalyticsComputor.totalObservedVolume("NVDA"));
    assertEquals(150, basicAnalyticsComputor.totalObservedVolume("AAPL"));
  }

  @Test
  public void mostActiveStock_noData() {
    when(dataStore.knownSymbols()).thenReturn(Collections.emptySet());

    String mostActiveStock = basicAnalyticsComputor.mostActiveStock();
    assertNull(mostActiveStock);
  }

  @Test
  public void mostActiveStock_singleDataPoint() {
    when(dataStore.knownSymbols()).thenReturn(Set.of("NVDA"));

    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("NVDA", 137.93, TEST_TIME.toEpochMilli(), 250)));

    assertEquals("NVDA", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeNotMostDataPoints() {
    when(dataStore.knownSymbols()).thenReturn(Set.of("NVDA", "AAPL"));

    when(dataStore.getHistory("AAPL"))
        .thenReturn(
            ImmutableList.of(new FinhubResponse("AAPL", 150.25, TEST_TIME.toEpochMilli(), 500)));
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 135.91, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 50),
                new FinhubResponse(
                    "NVDA", 136.37, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 50),
                new FinhubResponse("NVDA", 136.19, TEST_TIME.toEpochMilli(), 50)));

    assertEquals("AAPL", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void mostActiveStock_multpleStocks_largestVolumeAlsoMostDataPoints() {
    when(dataStore.knownSymbols()).thenReturn(Set.of("NVDA", "AAPL"));

    when(dataStore.getHistory("AAPL"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "AAPL", 150.25, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 500),
                new FinhubResponse(
                    "AAPL", 152.87, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 500),
                new FinhubResponse("AAPL", 151.92, TEST_TIME.toEpochMilli(), 500)));
    when(dataStore.getHistory("NVDA"))
        .thenReturn(
            ImmutableList.of(
                new FinhubResponse(
                    "NVDA", 136.52, TEST_TIME.plus(1, ChronoUnit.SECONDS).toEpochMilli(), 50)));

    assertEquals("AAPL", basicAnalyticsComputor.mostActiveStock());
  }

  @Test
  public void knownSymbols_single() {
    when(dataStore.knownSymbols()).thenReturn(Set.of("AAPL"));

    assertEquals(Set.of("AAPL"), basicAnalyticsComputor.knownSymbols());
  }
}

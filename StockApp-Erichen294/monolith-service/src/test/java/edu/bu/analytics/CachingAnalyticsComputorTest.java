package edu.bu.analytics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.ImmutableList;
import edu.bu.data.InMemoryStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CachingAnalyticsComputorTest {
  static final Instant TEST_TIME = Instant.ofEpochMilli(1722470400000L);

  CachingAnalyticsComputor cachingAnalyticsComputor;
  BasicAnalyticsComputor basicAnalyticsComputor;
  InMemoryStore dataStore;

  @BeforeEach
  public void setUp() {
    dataStore = new InMemoryStore();
    basicAnalyticsComputor = new BasicAnalyticsComputor(dataStore);
    cachingAnalyticsComputor = new CachingAnalyticsComputor(basicAnalyticsComputor, dataStore);
  }

  @Test
  public void totalObservedVolume_singleCall() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);

    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_subsequentCallsWithNoUpdateComeFromCache()
      throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));

    // we are going to put a fake, incorrect marker response in the cache to allow us to easily
    // verify that the cache
    // was used and not cleared since last call - it should not have been because there have been no
    // updates
    cachingAnalyticsComputor.totalObservedVolumeCache.put("NVDA", -999L);
    assertEquals(-999L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }

  @Test
  public void totalObservedVolume_cacheClearedOnUpdate() throws UnknownSymbolException {
    List<FinhubResponse> singleResponse =
        ImmutableList.of(new FinhubResponse("NVDA", 134.12, TEST_TIME.toEpochMilli(), 100));

    dataStore.update(singleResponse);
    assertEquals(100L, cachingAnalyticsComputor.totalObservedVolume("NVDA"));

    // we are going to put a fake, incorrect marker response in the cache and then send an update
    // through the data
    // store - this marker value should be dropped and caching computer should have correct result
    cachingAnalyticsComputor.totalObservedVolumeCache.put("NVDA", -999L);

    List<FinhubResponse> secondResponse =
        ImmutableList.of(
            new FinhubResponse(
                "NVDA", 134.12, TEST_TIME.plus(4, ChronoUnit.SECONDS).toEpochMilli(), 18));
    dataStore.update(secondResponse);

    assertEquals(118, cachingAnalyticsComputor.totalObservedVolume("NVDA"));
  }
}

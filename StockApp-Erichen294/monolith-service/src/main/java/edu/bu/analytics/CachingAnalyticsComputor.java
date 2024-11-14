package edu.bu.analytics;

import edu.bu.data.DataStore;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The caching analytics computer relies on the basicAnalyticsComputor for all methods except
 * totalObservedVolume. It will delegate computations to basicAnalyticsComputor. The caching
 * analytics computor has a cache that will be cleared if a new data point is encountered in the
 * dataStore. It will attempt to fetch data from the cache if available, otherwise it will delegate
 * it to the basicAnalyticsComputor and store the result.
 */
public class CachingAnalyticsComputor implements AnalyticsComputor {
  final BasicAnalyticsComputor basicAnalyticsComputor;
  final DataStore dataStore;
  int lastUpdateCount;

  public CachingAnalyticsComputor(
      BasicAnalyticsComputor basicAnalyticsComputor, DataStore dataStore) {
    this.basicAnalyticsComputor = basicAnalyticsComputor;
    this.dataStore = dataStore;
    this.lastUpdateCount = dataStore.numberOfUpdatesSeen();
  }

  /** Stock symbol keyed hash of total observed volume results. */
  Map<String, Long> totalObservedVolumeCache = new HashMap<>();

  // leaving the rest for you to implement, but here is an example of how to delegate
  // to a method that is not one that we will be using the cache for:
  @Override
  public Set<String> knownSymbols() {
    return basicAnalyticsComputor.knownSymbols();
  }

  @Override
  public long totalObservedVolume(String symbol) throws UnknownSymbolException {
    basicAnalyticsComputor.checkSymbolExists(symbol);

    if (dataStore.numberOfUpdatesSeen() != lastUpdateCount) {
      totalObservedVolumeCache.clear();
      lastUpdateCount = dataStore.numberOfUpdatesSeen();
    }

    if (totalObservedVolumeCache.containsKey(symbol)) {
      return totalObservedVolumeCache.get(symbol);
    }

    long volume = basicAnalyticsComputor.totalObservedVolume(symbol);

    totalObservedVolumeCache.put(symbol, volume);

    return volume;
  }

  @Override
  public double averageVolumePerSecond(String symbol)
      throws InvalidSymbolException, UnknownSymbolException {
    return basicAnalyticsComputor.averageVolumePerSecond(symbol);
  }

  @Override
  public String mostActiveStock() {
    return basicAnalyticsComputor.mostActiveStock();
  }

  @Override
  public String mostActiveStock(Instant startTime, Instant endTime) {
    return basicAnalyticsComputor.mostActiveStock(startTime, endTime);
  }

  @Override
  public double currentPrice(String symbol) throws UnknownSymbolException {
    return basicAnalyticsComputor.currentPrice(symbol);
  }
}

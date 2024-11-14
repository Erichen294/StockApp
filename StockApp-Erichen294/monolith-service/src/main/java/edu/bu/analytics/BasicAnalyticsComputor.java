package edu.bu.analytics;

import edu.bu.data.DataStore;
import edu.bu.finhub.FinhubResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The analytics computer relies on the {@link DataStore} for historic stock price and volume data
 * points. It uses these data points to compute higher level metrics about individual as well as
 * aggregate stocks.
 */
public class BasicAnalyticsComputor implements AnalyticsComputor {
  // exposes historic data points for the computations performed here
  final DataStore dataStore;

  /** Construct an instance by providing the {@link DataStore} collaborator. */
  public BasicAnalyticsComputor(DataStore dataStore) {
    this.dataStore = dataStore;
  }

  @Override
  public Set<String> knownSymbols() {
    return dataStore.knownSymbols();
  }

  @Override
  public long totalObservedVolume(String symbol) throws UnknownSymbolException {
    checkSymbolExists(symbol);

    return dataStore.getHistory(symbol).stream()
        .collect(Collectors.summingLong(data -> data.volume));
  }

  @Override
  public double averageVolumePerSecond(String symbol)
      throws InvalidSymbolException, UnknownSymbolException {
    checkValidSymbol(symbol);

    checkSymbolExists(symbol);

    List<FinhubResponse> stockHistory = dataStore.getHistory(symbol);

    long totalVolume = 0;
    long startTime = stockHistory.get(0).msSinceEpoch;
    long endTime = stockHistory.get(stockHistory.size() - 1).msSinceEpoch;
    double totalDuration =
        (endTime - startTime) != 0 ? Math.abs(endTime - startTime) / 1000.0 : 1.0;

    for (FinhubResponse response : stockHistory) {
      totalVolume += response.volume;
    }
    return totalVolume / totalDuration;
  }

  @Override
  public String mostActiveStock() {
    Map<String, Long> totalVolumeBySymbol =
        dataStore.knownSymbols().stream()
            .collect(
                // Collectors.toMap takes two lambdas (functions) one that transforms input to the
                // keys of the map
                // and another that transforms the input to the values of the map.
                Collectors.toMap(
                    symbol -> symbol,
                    symbol ->
                        dataStore.getHistory(symbol).stream()
                            .collect(Collectors.summingLong(data -> data.volume))));

    Optional<Map.Entry<String, Long>> result =
        totalVolumeBySymbol.entrySet().stream().max(Map.Entry.comparingByValue());

    return result.isPresent() ? result.get().getKey() : null;
  }

  /**
   * @param startTime inclusive start of the window of interest
   * @param endTime inclusive end of the window of interest
   * @return of all the stocks that the data store is aware of, the symbol of the stock that has
   *     recorded the highest total trade volume between startTime and endTime, inclusive.
   */
  @Override
  public String mostActiveStock(Instant startTime, Instant endTime) {
    String mostActive = null;
    long highestVolume = 0;

    for (String symbol : dataStore.knownSymbols()) {
      long currVolume = 0;
      for (FinhubResponse response : dataStore.getHistory(symbol)) {
        Instant timestamp = Instant.ofEpochMilli(response.msSinceEpoch);
        if (!timestamp.isBefore(startTime) && !timestamp.isAfter(endTime)) {
          currVolume += response.volume;
        }
      }

      if (currVolume > highestVolume) {
        highestVolume = currVolume;
        mostActive = symbol;
      }
    }
    return mostActive;
  }

  /**
   * @param symbol stock symbol of interest
   * @return the most recently reported price for the stock
   * @throws UnknownSymbolException if the requested stock symbol has not been seen by the data
   *     store
   */
  public double currentPrice(String symbol) throws UnknownSymbolException {
    checkSymbolExists(symbol);

    return dataStore.getHistory(symbol).get(0).price;
  }

  /**
   * Internal helper for checking whether a given stock symbol has been seen by our data store.
   *
   * @param symbol stock symbol of interest
   * @throws UnknownSymbolException if the data store is not aware of the requested symbol.
   */
  void checkSymbolExists(String symbol) throws UnknownSymbolException {
    if (!dataStore.haveSymbol(symbol)) {
      throw new UnknownSymbolException(symbol);
    }
  }

  /**
   * Internal helper to check if a given stock symbol is valid. Valid stock-symbols are between 1
   * and 5 alphanumeric characters. No special characters are allowed.
   *
   * @param symbol stock symbol of interest
   * @throws InvalidSymbolException if the symbol is not valid
   */
  void checkValidSymbol(String symbol) throws InvalidSymbolException {
    if (symbol == null || symbol.isEmpty()) {
      throw new InvalidSymbolException(symbol);
    }

    String specialCharacters = "[^a-zA-Z0-9 ]";

    if (symbol.matches(".*" + specialCharacters + ".*")) {
      throw new InvalidSymbolException(symbol);
    }

    if (!symbol.matches("^[a-zA-z0-9]{1,5}$")) {
      throw new InvalidSymbolException(symbol);
    }
  }
}

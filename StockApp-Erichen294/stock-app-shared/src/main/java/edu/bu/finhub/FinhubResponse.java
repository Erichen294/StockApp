package edu.bu.finhub;

import java.time.Instant;

/**
 * Data class representing the four piecs of information that we receive from FinHub on a stock
 * update event.
 */
public class FinhubResponse {
  public final String symbol;
  public final double price;
  public final long msSinceEpoch;
  public final long volume;

  public FinhubResponse(String symbol, double price, long msSinceEpoch, long volume) {
    this.symbol = symbol;
    this.price = price;
    this.msSinceEpoch = msSinceEpoch;
    this.volume = volume;
  }

  @Override
  public String toString() {
    return "FinhubResponse{"
        + "symbol='"
        + symbol
        + '\''
        + ", price="
        + price
        + ", msSinceEpoch="
        + Instant.ofEpochMilli(msSinceEpoch)
        + ", volume="
        + volume
        + '}';
  }
}

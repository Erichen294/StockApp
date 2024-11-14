package edu.bu.finhub.persistence;

import java.time.Instant;

/** Simple representation of a stored symbol and its creation date in storage. */
public class StoredSymbol {
  public final String symbol;
  public final Instant createdAt;

  public StoredSymbol(String symbol, Instant createdAt) {
    this.symbol = symbol;
    this.createdAt = createdAt;
  }
}

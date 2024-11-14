package edu.bu.analytics;

/**
 * Thrown when StockApp is asked a question about a Stock symbol that it has not been registered to
 * follow.
 */
public class UnknownSymbolException extends Exception {
  final String symbol;

  public UnknownSymbolException(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String getMessage() {
    return symbol + " has not been seen by the server";
  }
}

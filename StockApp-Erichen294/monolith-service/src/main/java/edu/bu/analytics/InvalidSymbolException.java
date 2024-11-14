package edu.bu.analytics;

/** Thrown when StockApp is asked a question about a Stock symbol that invalid */
public class InvalidSymbolException extends Exception {
  final String symbol;

  public InvalidSymbolException(String symbol) {
    this.symbol = symbol;
  }

  @Override
  public String getMessage() {
    return symbol + " is not a valid symbol";
  }
}

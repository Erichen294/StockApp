package edu.bu.utils;

/** Class that verifies if a stock symbol is valid */
public class VerifyStockSymbol {
  /**
   * Checks if a given stock symbol is valid. Valid stock-symbols are between 1 and 5 alphanumeric
   * characters. No special characters are allowed.
   *
   * @param symbol stock symbol of interest
   */
  public boolean checkValidSymbol(String symbol) {
    if (symbol == null || symbol.isEmpty()) {
      return false;
    }

    String specialCharacters = "[^a-zA-Z0-9 ]";

    if (symbol.matches(".*" + specialCharacters + ".*")) {
      return false;
    }

    return symbol.matches("^[a-zA-z0-9]{1,5}$");
  }
}

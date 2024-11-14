package edu.bu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.bu.utils.VerifyStockSymbol;
import org.junit.jupiter.api.Test;

class VerifyStockSymbolTest {

  // Instantiate the class under test
  VerifyStockSymbol verifyStockSymbol = new VerifyStockSymbol();

  @Test
  void testValidSymbol() {
    // Test a valid stock symbol (1 to 5 alphanumeric characters)
    assertTrue(verifyStockSymbol.checkValidSymbol("AAPL"));
    assertTrue(verifyStockSymbol.checkValidSymbol("TSLA"));
    assertTrue(verifyStockSymbol.checkValidSymbol("GOOG"));
    assertTrue(verifyStockSymbol.checkValidSymbol("X"));
    assertTrue(verifyStockSymbol.checkValidSymbol("12345"));
  }

  @Test
  void testInvalidSymbolTooLong() {
    // Test a symbol that is too long (more than 5 characters)
    assertFalse(verifyStockSymbol.checkValidSymbol("AAPLGO"));
    assertFalse(verifyStockSymbol.checkValidSymbol("LONGSYMBOL"));
    assertFalse(verifyStockSymbol.checkValidSymbol("123456"));
  }

  @Test
  void testInvalidSymbolWithSpecialCharacters() {
    // Test symbols with special characters
    assertFalse(verifyStockSymbol.checkValidSymbol("AAPL$"));
    assertFalse(verifyStockSymbol.checkValidSymbol("TSL@"));
    assertFalse(verifyStockSymbol.checkValidSymbol("MSF!"));
    assertFalse(verifyStockSymbol.checkValidSymbol("123%"));
    assertFalse(verifyStockSymbol.checkValidSymbol("ABC#"));
  }

  @Test
  void testNullOrEmptySymbol() {
    // Test null and empty string cases
    assertFalse(verifyStockSymbol.checkValidSymbol(null));
    assertFalse(verifyStockSymbol.checkValidSymbol(""));
  }

  @Test
  void testValidSingleCharacterSymbols() {
    // Test single-character valid symbols
    assertTrue(verifyStockSymbol.checkValidSymbol("A"));
    assertTrue(verifyStockSymbol.checkValidSymbol("Z"));
    assertTrue(verifyStockSymbol.checkValidSymbol("9"));
  }
}

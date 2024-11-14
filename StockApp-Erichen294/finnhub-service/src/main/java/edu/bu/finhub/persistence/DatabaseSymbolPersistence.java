package edu.bu.finhub.persistence;

import java.sql.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/** Implementation of SymbolsPersistence that uses a database as persistent storage. * */
public class DatabaseSymbolPersistence implements SymbolsPersistence {
  private static final String DB_URL = "jdbc:sqlite:symbolpersistence.db";

  @Override
  public void add(String symbol) {
    String query = "INSERT INTO subscribed_symbols(symbol) VALUES(?)";

    try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, symbol);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void remove(String symbol) {
    String query = "DELETE FROM subscribed_symbols WHERE symbol = ?";

    try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(query)) {
      pstmt.setString(1, symbol);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    String query = "SELECT symbol, added_at FROM subscribed_symbols";
    Set<StoredSymbol> storedSymbols = new HashSet<>();

    try (Connection conn = DriverManager.getConnection(DB_URL);
        PreparedStatement pstmt = conn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery()) {
      while (result.next()) {
        String symbol = result.getString("symbol");

        // Retrieve timestamp from db
        Timestamp addedAtAsTimestamp = result.getTimestamp("added_at");
        Instant addedAt = addedAtAsTimestamp.toInstant();

        storedSymbols.add(new StoredSymbol(symbol, addedAt));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return storedSymbols;
  }
}

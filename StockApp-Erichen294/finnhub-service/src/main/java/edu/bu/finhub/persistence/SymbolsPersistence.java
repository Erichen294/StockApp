package edu.bu.finhub.persistence;

import java.util.Set;

/** Implementors manage symbols currently in persistence. */
public interface SymbolsPersistence {
  void add(String symbol);

  void remove(String symbol);

  Set<StoredSymbol> readAll();
}

package edu.bu.finhub.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/** No-op implementation for SymbolsPersistence. * */
public class FileBasedSymbolPersistence implements SymbolsPersistence {
  private static final Path STORAGE_PATH = Paths.get("symbol-storage");
  private static final long VALID_DURATION_DAYS = 10;

  @Override
  public void add(String symbol) {
    try {
      Path symbolFile = STORAGE_PATH.resolve(symbol);
      if (!Files.exists(symbolFile)) {
        Files.createFile(symbolFile);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to add symbol to storage", e);
    }
  }

  @Override
  public void remove(String symbol) {
    try {
      Path symbolFile = STORAGE_PATH.resolve(symbol);
      if (Files.exists(symbolFile)) {
        Files.delete(symbolFile);
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to remove symbol from storage", e);
    }
  }

  @Override
  public Set<StoredSymbol> readAll() {
    try {
      return Files.walk(STORAGE_PATH)
          .filter(Files::isRegularFile)
          .map(this::toStoredSymbol)
          .filter(symbol -> isValid(symbol.createdAt))
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read all symbols", e);
    }
  }

  // Helper function to turn symbol file into StoredSymbol
  private StoredSymbol toStoredSymbol(Path file) {
    try {
      BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
      Instant createdAt = attrs.creationTime().toInstant();
      return new StoredSymbol(file.getFileName().toString(), createdAt);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file attributes for " + file, e);
    }
  }

  // Helper function to determine if time of creation is not more than 10 days ago
  private boolean isValid(Instant createdAt) {
    Instant tenDaysAgo = Instant.now().minusSeconds(VALID_DURATION_DAYS * 24 * 60 * 60);
    return !createdAt.isBefore(tenDaysAgo);
  }
}

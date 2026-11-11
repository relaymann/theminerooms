package com.relaymann.theminerooms.level;
import java.util.List;
import java.util.Objects;
/** Immutable level metadata. Drafts may be incomplete until release validation. */
public record LevelDefinition(String id, String displayName, String category, String sourceUrl, List<LevelConnection> entrances, List<LevelConnection> exits) {
 public LevelDefinition {
  id = text(id, "id"); displayName = text(displayName, "displayName"); category = text(category, "category"); sourceUrl = text(sourceUrl, "sourceUrl");
  entrances = List.copyOf(Objects.requireNonNull(entrances, "entrances")); exits = List.copyOf(Objects.requireNonNull(exits, "exits"));
  if (!id.matches("[a-z0-9_.-]+")) throw new IllegalArgumentException("Invalid lowercase level id: " + id);
 }
 private static String text(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank"); return value.trim(); }
}

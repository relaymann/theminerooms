package com.relaymann.theminerooms.level;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
/** In-memory catalog API; lore entries are added only after source review. */
public final class LevelCatalog {
 private final Map<String, LevelDefinition> levels = new LinkedHashMap<>();
 public synchronized void register(LevelDefinition definition) { if (levels.containsKey(definition.id())) throw new IllegalArgumentException("Duplicate level id: " + definition.id()); levels.put(definition.id(), definition); }
 public synchronized Optional<LevelDefinition> get(String id) { return Optional.ofNullable(levels.get(id)); }
 public synchronized Collection<LevelDefinition> all() { return Collections.unmodifiableList(levels.values().stream().toList()); }
 public synchronized int size() { return levels.size(); }
}

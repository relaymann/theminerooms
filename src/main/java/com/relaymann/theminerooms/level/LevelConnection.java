package com.relaymann.theminerooms.level;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
/** A source-backed transition to or from a documented endpoint. */
public record LevelConnection(String id, ConnectionKind kind, ConnectionTargetType targetType, String targetId, DiscoveryMethod discoveryMethod, ConnectionRarity rarity, String conditions, String sourceUrl, String sourceNotes) {
 public LevelConnection {
  id = text(id, "id"); kind = Objects.requireNonNull(kind, "kind"); targetType = Objects.requireNonNull(targetType, "targetType"); targetId = text(targetId, "targetId");
  discoveryMethod = Objects.requireNonNull(discoveryMethod, "discoveryMethod"); rarity = Objects.requireNonNull(rarity, "rarity");
  conditions = text(conditions, "conditions"); sourceUrl = text(sourceUrl, "sourceUrl"); sourceNotes = text(sourceNotes, "sourceNotes");
  try { URI uri = new URI(sourceUrl); if (!"https".equalsIgnoreCase(uri.getScheme()) || uri.getHost() == null) throw new IllegalArgumentException("sourceUrl must be absolute HTTPS"); }
  catch (URISyntaxException e) { throw new IllegalArgumentException("Invalid sourceUrl", e); }
 }
 private static String text(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank"); return value.trim(); }
}

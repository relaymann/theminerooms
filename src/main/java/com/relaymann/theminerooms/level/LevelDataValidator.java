package com.relaymann.theminerooms.level;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/** Release gate for source-backed level connection data. */
public final class LevelDataValidator {
 private LevelDataValidator() { }
 public static List<String> validate(List<LevelDefinition> levels) {
  List<String> issues = new ArrayList<>(); Set<String> known = new HashSet<>();
  for (LevelDefinition level : levels) if (!known.add(level.id())) issues.add("Duplicate level id: " + level.id());
  for (LevelDefinition level : levels) {
   connections(level, level.entrances(), ConnectionKind.ENTRANCE, known, issues);
   connections(level, level.exits(), ConnectionKind.EXIT, known, issues);
   source(level.id(), level.sourceUrl(), issues);
  }
  return List.copyOf(issues);
 }
 private static void connections(LevelDefinition level, List<LevelConnection> list, ConnectionKind expected, Set<String> known, List<String> issues) {
  String label = level.id() + " " + expected.name().toLowerCase();
  if (list.isEmpty() || list.size() > 2) issues.add(label + " must contain one or two documented connections; found " + list.size());
  Set<String> ids = new HashSet<>();
  for (LevelConnection c : list) {
   if (!ids.add(c.id())) issues.add(level.id() + " duplicate connection id: " + c.id());
   if (c.kind() != expected) issues.add(level.id() + " connection " + c.id() + " has incorrect kind");
   if (c.targetType() == ConnectionTargetType.LEVEL && !known.contains(c.targetId())) issues.add(level.id() + " connection " + c.id() + " targets unknown level: " + c.targetId());
   source(level.id() + " connection " + c.id(), c.sourceUrl(), issues);
  }
 }
 private static void source(String label, String url, List<String> issues) {
  if (url == null || !url.startsWith("https://backrooms.fandom.com/wiki/")) issues.add(label + " must cite an individual Backrooms Fandom Wiki page");
 }
}

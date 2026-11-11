package com.relaymann.theminerooms.level;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
class LevelDataValidatorTest {
 private static final String SOURCE = "https://backrooms.fandom.com/wiki/Level_0";
 @Test void acceptsSourcedLevelWithOneEntranceAndExit() {
  LevelDefinition zero = new LevelDefinition("level_0", "Level 0", "numbered", SOURCE, List.of(connection("entry", ConnectionKind.ENTRANCE, "level_1")), List.of(connection("exit", ConnectionKind.EXIT, "level_1")));
  LevelDefinition one = new LevelDefinition("level_1", "Level 1", "numbered", "https://backrooms.fandom.com/wiki/Level_1", List.of(connection("entry", ConnectionKind.ENTRANCE, "level_0")), List.of(connection("exit", ConnectionKind.EXIT, "level_0")));
  assertTrue(LevelDataValidator.validate(List.of(zero, one)).isEmpty());
 }
 @Test void rejectsMissingConnectionsAndUnknownTargets() {
  LevelDefinition draft = new LevelDefinition("level_draft", "Draft", "draft", SOURCE, List.of(), List.of(connection("exit", ConnectionKind.EXIT, "missing_level")));
  List<String> issues = LevelDataValidator.validate(List.of(draft));
  assertEquals(2, issues.size()); assertTrue(issues.stream().anyMatch(s -> s.contains("one or two"))); assertTrue(issues.stream().anyMatch(s -> s.contains("unknown level")));
 }
 private static LevelConnection connection(String id, ConnectionKind kind, String target) {
  return new LevelConnection(id, kind, target, DiscoveryMethod.NATURAL, ConnectionRarity.UNKNOWN, "Test-only condition; replace with reviewed source wording.", SOURCE, "Test fixture only; not shipped lore data.");
 }
}

package com.relaymann.theminerooms.level;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.junit.jupiter.api.Test;
class LevelDataValidatorTest {
 private static final String SOURCE_A = "https://backrooms.fandom.com/wiki/Test_Fixture_A";
 @Test void acceptsLevelAndRealityEndpointsWithOneEntranceAndExit() {
  LevelDefinition a = new LevelDefinition("fixture_a", "Fixture A", "test", SOURCE_A,
   List.of(connection("entry_from_reality", ConnectionKind.ENTRANCE, ConnectionTargetType.REALITY, "reality")),
   List.of(connection("exit_to_b", ConnectionKind.EXIT, ConnectionTargetType.LEVEL, "fixture_b")));
  LevelDefinition b = new LevelDefinition("fixture_b", "Fixture B", "test", "https://backrooms.fandom.com/wiki/Test_Fixture_B",
   List.of(connection("entry_from_a", ConnectionKind.ENTRANCE, ConnectionTargetType.LEVEL, "fixture_a")),
   List.of(connection("exit_to_location", ConnectionKind.EXIT, ConnectionTargetType.NON_LEVEL_LOCATION, "safe_location")));
  assertTrue(LevelDataValidator.validate(List.of(a, b)).isEmpty());
 }
 @Test void rejectsMissingConnectionsAndUnknownLevelTargets() {
  LevelDefinition draft = new LevelDefinition("fixture_draft", "Draft", "test", SOURCE_A, List.of(),
   List.of(connection("exit", ConnectionKind.EXIT, ConnectionTargetType.LEVEL, "missing_level")));
  List<String> issues = LevelDataValidator.validate(List.of(draft));
  assertEquals(2, issues.size());
  assertTrue(issues.stream().anyMatch(s -> s.contains("one or two")));
  assertTrue(issues.stream().anyMatch(s -> s.contains("unknown level")));
 }

 @Test void rejectsDuplicateConnectionIdsAcrossEntrancesAndExits() {
  LevelConnection sharedEntrance = connection("shared_id", ConnectionKind.ENTRANCE,
   ConnectionTargetType.REALITY, "reality");
  LevelConnection sharedExit = connection("shared_id", ConnectionKind.EXIT,
   ConnectionTargetType.NON_LEVEL_LOCATION, "safe_location");
  LevelDefinition level = new LevelDefinition("fixture_a", "Fixture A", "test", SOURCE_A,
   List.of(sharedEntrance), List.of(sharedExit));
  assertTrue(LevelDataValidator.validate(List.of(level)).stream()
   .anyMatch(issue -> issue.contains("duplicate connection id: shared_id")));
 }

 @Test void rejectsWikiUrlHostSpoofingAndEmptyWikiPagePaths() {
  LevelDefinition spoofed = new LevelDefinition("fixture_a", "Fixture A", "test",
   "https://backrooms.fandom.com.evil.example/wiki/Level_0",
   List.of(connection("entry", ConnectionKind.ENTRANCE, ConnectionTargetType.REALITY, "reality")),
   List.of(connection("exit", ConnectionKind.EXIT, ConnectionTargetType.NON_LEVEL_LOCATION, "safe")));
  LevelDefinition emptyPage = new LevelDefinition("fixture_b", "Fixture B", "test",
   "https://backrooms.fandom.com/wiki/",
   List.of(connection("entry_b", ConnectionKind.ENTRANCE, ConnectionTargetType.REALITY, "reality")),
   List.of(connection("exit_b", ConnectionKind.EXIT, ConnectionTargetType.NON_LEVEL_LOCATION, "safe")));
  assertTrue(LevelDataValidator.validate(List.of(spoofed)).stream()
   .anyMatch(issue -> issue.contains("individual Backrooms Fandom Wiki page")));
  assertTrue(LevelDataValidator.validate(List.of(emptyPage)).stream()
   .anyMatch(issue -> issue.contains("individual Backrooms Fandom Wiki page")));
 }

 @Test void rejectsNullLevelCollectionAndNullLevelEntriesClearly() {
  org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
   () -> LevelDataValidator.validate(null));
  assertTrue(LevelDataValidator.validate(java.util.Arrays.asList((LevelDefinition) null)).stream()
   .anyMatch(issue -> issue.contains("null entries")));
 }
\n private static LevelConnection connection(String id, ConnectionKind kind, ConnectionTargetType targetType, String targetId) {
  return new LevelConnection(id, kind, targetType, targetId, DiscoveryMethod.NATURAL, ConnectionRarity.UNKNOWN,
   "Test-only condition; replace with reviewed source wording.", SOURCE_A, "Test fixture only; not shipped lore data.");
 }
}

# Level and connection data

The Java records in `level/` are the initial typed API. The JSON below is illustrative, not shipped lore content.

## Level fields

- `id`: stable lowercase internal ID.
- `displayName`: source-aligned name.
- `category`: documented wiki classification.
- `sourceUrl`: individual Fandom Wiki page.
- `entrances`: one or two selected documented incoming connections.
- `exits`: one or two selected documented outgoing connections.

## Connection fields

- `id`: unique within the level.
- `kind`: `ENTRANCE` or `EXIT`.
- `targetLevelId`: documented origin or destination identifier.
- `discoveryMethod`: natural, environmental clue, puzzle, lore clue, or conditional.
- `rarity`: qualitative implementation weight; use `UNKNOWN` when source does not support a rarity claim.
- `conditions`: concise source-backed conditions.
- `sourceUrl`: individual page supporting this transition.
- `sourceNotes`: page section/review notes, not unsourced assumptions.

## Example (illustrative only)

```json
{
  "id": "example_level",
  "displayName": "Example Level",
  "category": "numbered",
  "sourceUrl": "https://backrooms.fandom.com/wiki/Example_Level",
  "entrances": [{"id":"entry_example","kind":"ENTRANCE","targetLevelId":"documented_origin","discoveryMethod":"NATURAL","rarity":"UNKNOWN","conditions":"Replace with source-backed condition.","sourceUrl":"https://backrooms.fandom.com/wiki/Example_Level","sourceNotes":"Add section and review notes."}],
  "exits": [{"id":"exit_example","kind":"EXIT","targetLevelId":"documented_destination","discoveryMethod":"CONDITIONAL","rarity":"UNKNOWN","conditions":"Replace with source-backed condition.","sourceUrl":"https://backrooms.fandom.com/wiki/Example_Level","sourceNotes":"Add section and review notes."}]
}
```

Example IDs and conditions are placeholders and must not ship as lore. Validate the complete release catalog before packaging.

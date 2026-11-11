package com.relaymann.theminerooms.level;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Release gate for source-backed level connection data. */
public final class LevelDataValidator {
    private static final String WIKI_HOST = "backrooms.fandom.com";
    private static final String WIKI_PATH_PREFIX = "/wiki/";

    private LevelDataValidator() {
    }

    public static List<String> validate(List<LevelDefinition> levels) {
        if (levels == null) {
            throw new IllegalArgumentException("levels must not be null");
        }

        List<String> issues = new ArrayList<>();
        Set<String> known = new HashSet<>();
        for (LevelDefinition level : levels) {
            if (level == null) {
                issues.add("Level list must not contain null entries");
            } else if (!known.add(level.id())) {
                issues.add("Duplicate level id: " + level.id());
            }
        }

        for (LevelDefinition level : levels) {
            if (level == null) {
                continue;
            }
            Set<String> connectionIds = new HashSet<>();
            connections(level, level.entrances(), ConnectionKind.ENTRANCE, known, connectionIds, issues);
            connections(level, level.exits(), ConnectionKind.EXIT, known, connectionIds, issues);
            source(level.id(), level.sourceUrl(), issues);
        }
        return List.copyOf(issues);
    }

    private static void connections(
            LevelDefinition level,
            List<LevelConnection> list,
            ConnectionKind expected,
            Set<String> known,
            Set<String> connectionIds,
            List<String> issues) {
        String label = level.id() + " " + expected.name().toLowerCase();
        if (list.isEmpty() || list.size() > 2) {
            issues.add(label + " must contain one or two documented connections; found " + list.size());
        }

        for (LevelConnection connection : list) {
            if (!connectionIds.add(connection.id())) {
                issues.add(level.id() + " duplicate connection id: " + connection.id());
            }
            if (connection.kind() != expected) {
                issues.add(level.id() + " connection " + connection.id() + " has incorrect kind");
            }
            if (connection.targetType() == ConnectionTargetType.LEVEL
                    && !known.contains(connection.targetId())) {
                issues.add(level.id() + " connection " + connection.id()
                        + " targets unknown level: " + connection.targetId());
            }
            source(level.id() + " connection " + connection.id(), connection.sourceUrl(), issues);
        }
    }

    private static void source(String label, String url, List<String> issues) {
        try {
            URI uri = new URI(url);
            String path = uri.getRawPath();
            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !WIKI_HOST.equalsIgnoreCase(uri.getHost())
                    || uri.getUserInfo() != null
                    || path == null
                    || !path.startsWith(WIKI_PATH_PREFIX)
                    || path.length() == WIKI_PATH_PREFIX.length()) {
                issues.add(label + " must cite an individual Backrooms Fandom Wiki page");
            }
        } catch (URISyntaxException | NullPointerException exception) {
            issues.add(label + " must cite an individual Backrooms Fandom Wiki page");
        }
    }
}

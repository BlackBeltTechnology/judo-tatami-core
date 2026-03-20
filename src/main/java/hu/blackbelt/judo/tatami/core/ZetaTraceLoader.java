package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2024 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * TraceLoader implementation for Zeta JSON trace format.
 *
 * <p>The Zeta JSON format uses the following structure:</p>
 * <pre>{@code
 * {
 *   "traceEntries": [
 *     {
 *       "ruleName": "Entity2Table",
 *       "source": { "type": "EntityType", "id": "entity-1", "name": "Customer" },
 *       "target": { "type": "Table", "id": "table-1", "name": "Customer" },
 *       "primary": true,
 *       "discriminator": null
 *     }
 *   ],
 *   "entryCount": 1,
 *   "timestamp": 1699123456789
 * }
 * }</pre>
 *
 * <p>Element resolution uses the 'id' EAttribute of EObjects to match against
 * the 'id' field in the JSON trace entries. If no 'id' EAttribute exists,
 * the URI fragment (XMI ID) is used as a fallback.</p>
 *
 * <p>Entries with the same source, ruleName, and discriminator are grouped
 * into a single TraceEntry with multiple targets.</p>
 */
@Slf4j
public class ZetaTraceLoader implements TraceLoader {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public List<TraceEntry> loadTrace(InputStream input, List<ResourceSet> resourcesToResolve)
            throws TraceLoadException {
        try {
            // Build ID index for fast resolution
            Map<String, EObject> idIndex = buildIdIndex(resourcesToResolve);

            // Parse JSON
            BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            JsonArray traceEntries = root.getAsJsonArray("traceEntries");
            if (traceEntries == null) {
                return new ArrayList<>();
            }

            // Parse entries and group by source + ruleName + discriminator
            Map<GroupKey, List<EObject>> grouped = new LinkedHashMap<>();
            Map<GroupKey, Boolean> primaryFlags = new HashMap<>();

            for (JsonElement element : traceEntries) {
                JsonObject entry = element.getAsJsonObject();

                String ruleName = getStringOrNull(entry, "ruleName");
                String discriminator = getStringOrNull(entry, "discriminator");
                boolean primary = entry.has("primary") && entry.get("primary").getAsBoolean();

                // Resolve sources
                List<EObject> sources = resolveSources(entry, idIndex, ruleName);
                if (sources.isEmpty()) {
                    continue;
                }

                // Resolve targets
                List<EObject> targets = resolveTargets(entry, idIndex, ruleName);

                // Create group key using first source (for grouping)
                EObject primarySource = sources.get(0);
                GroupKey key = new GroupKey(primarySource, ruleName, discriminator);

                // Add targets to group
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).addAll(targets);

                // Track primary flag (OR logic - if any entry is primary, result is primary)
                if (primary) {
                    primaryFlags.put(key, true);
                }
            }

            // Convert grouped entries to TraceEntry objects
            List<TraceEntry> result = new ArrayList<>();
            for (Map.Entry<GroupKey, List<EObject>> e : grouped.entrySet()) {
                GroupKey key = e.getKey();
                result.add(TraceEntry.builder()
                        .source(key.source)
                        .targets(e.getValue())
                        .ruleName(key.ruleName)
                        .discriminator(key.discriminator)
                        .primary(primaryFlags.getOrDefault(key, false))
                        .build());
            }

            return result;

        } catch (TraceLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new TraceLoadException("Failed to parse Zeta JSON trace: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveTrace(List<TraceEntry> entries, OutputStream output) throws TraceSaveException {
        try {
            List<Map<String, Object>> traceData = new ArrayList<>();

            for (TraceEntry entry : entries) {
                // For each source-target combination, create a JSON entry
                for (EObject source : entry.getSources()) {
                    for (EObject target : entry.getTargets()) {
                        Map<String, Object> entryMap = new LinkedHashMap<>();
                        entryMap.put("ruleName", entry.getRuleName());
                        entryMap.put("source", toElementInfo(source));
                        entryMap.put("target", toElementInfo(target));
                        entryMap.put("primary", entry.isPrimary());

                        if (entry.getDiscriminator() != null) {
                            entryMap.put("discriminator", entry.getDiscriminator());
                        }

                        traceData.add(entryMap);
                    }
                }
            }

            Map<String, Object> root = new LinkedHashMap<>();
            root.put("traceEntries", traceData);
            root.put("entryCount", traceData.size());
            root.put("timestamp", System.currentTimeMillis());

            Writer writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
            gson.toJson(root, writer);
            writer.flush();

        } catch (Exception e) {
            throw new TraceSaveException("Failed to save Zeta JSON trace: " + e.getMessage(), e);
        }
    }

    private Map<String, EObject> buildIdIndex(List<ResourceSet> resourceSets) {
        Map<String, EObject> index = new HashMap<>();

        for (ResourceSet rs : resourceSets) {
            for (TreeIterator<Notifier> it = rs.getAllContents(); it.hasNext(); ) {
                Notifier notifier = it.next();
                if (notifier instanceof EObject) {
                    EObject eObject = (EObject) notifier;
                    // Index by id EAttribute
                    String id = getIdAttribute(eObject);
                    if (id != null) {
                        index.put(id, eObject);
                    }
                    // Also index by URI fragment (XMI ID) as fallback,
                    // matching what getElementId() writes during save
                    String fragment = EcoreUtil.getURI(eObject).fragment();
                    if (fragment != null && !index.containsKey(fragment)) {
                        index.put(fragment, eObject);
                    }
                }
            }
        }

        return index;
    }

    private String getIdAttribute(EObject eObject) {
        EStructuralFeature idFeature = eObject.eClass().getEStructuralFeature("id");
        if (idFeature != null) {
            Object idValue = eObject.eGet(idFeature);
            if (idValue != null) {
                return idValue.toString();
            }
        }
        return null;
    }

    private List<EObject> resolveSources(JsonObject entry, Map<String, EObject> idIndex, String ruleName) {
        List<EObject> sources = new ArrayList<>();

        // Handle single source
        if (entry.has("source") && !entry.get("source").isJsonNull()) {
            EObject source = resolveElement(entry.getAsJsonObject("source"), idIndex, ruleName);
            sources.add(source);
        }

        // Handle multiple sources
        if (entry.has("sources") && entry.get("sources").isJsonArray()) {
            for (JsonElement srcElement : entry.getAsJsonArray("sources")) {
                EObject source = resolveElement(srcElement.getAsJsonObject(), idIndex, ruleName);
                sources.add(source);
            }
        }

        return sources;
    }

    private List<EObject> resolveTargets(JsonObject entry, Map<String, EObject> idIndex, String ruleName) {
        List<EObject> targets = new ArrayList<>();

        // Handle single target
        if (entry.has("target") && !entry.get("target").isJsonNull()) {
            EObject target = resolveElement(entry.getAsJsonObject("target"), idIndex, ruleName);
            targets.add(target);
        }

        // Handle multiple targets
        if (entry.has("targets") && entry.get("targets").isJsonArray()) {
            for (JsonElement tgtElement : entry.getAsJsonArray("targets")) {
                EObject target = resolveElement(tgtElement.getAsJsonObject(), idIndex, ruleName);
                targets.add(target);
            }
        }

        return targets;
    }

    private EObject resolveElement(JsonObject elementInfo, Map<String, EObject> idIndex, String ruleName) {
        String id = getStringOrNull(elementInfo, "id");
        String type = getStringOrNull(elementInfo, "type");

        if (id == null) {
            throw new TraceLoadException("Element info missing 'id' field, ruleName=" + ruleName);
        }

        EObject resolved = idIndex.get(id);
        if (resolved == null) {
            throw new TraceLoadException("Element not found: id=" + id +
                    ", ruleName=" + ruleName +
                    ", type=" + type);
        }

        return resolved;
    }

    private String getStringOrNull(JsonObject obj, String field) {
        if (obj.has(field) && !obj.get(field).isJsonNull()) {
            return obj.get(field).getAsString();
        }
        return null;
    }

    private Map<String, Object> toElementInfo(EObject element) {
        Map<String, Object> info = new LinkedHashMap<>();

        info.put("type", element.eClass().getName());
        info.put("id", getElementId(element));

        // Try to get name if available
        EStructuralFeature nameFeature = element.eClass().getEStructuralFeature("name");
        if (nameFeature != null) {
            Object name = element.eGet(nameFeature);
            if (name != null) {
                info.put("name", name.toString());
            }
        }

        return info;
    }

    private String getElementId(EObject element) {
        // Try id attribute first
        String id = getIdAttribute(element);
        if (id != null) {
            return id;
        }

        // Fall back to URI fragment
        Resource resource = element.eResource();
        if (resource != null) {
            String fragment = resource.getURIFragment(element);
            if (fragment != null && !fragment.startsWith("/")) {
                return fragment;
            }
        }

        // Fallback to identity hash
        return "obj@" + Integer.toHexString(System.identityHashCode(element));
    }

    /**
     * Group key for consolidating entries with same source, ruleName, and discriminator.
     */
    private static final class GroupKey {
        private final EObject source;
        private final String ruleName;
        private final String discriminator;

        GroupKey(EObject source, String ruleName, String discriminator) {
            this.source = source;
            this.ruleName = ruleName;
            this.discriminator = discriminator;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupKey groupKey = (GroupKey) o;
            return source == groupKey.source &&
                    Objects.equals(ruleName, groupKey.ruleName) &&
                    Objects.equals(discriminator, groupKey.discriminator);
        }

        @Override
        public int hashCode() {
            return Objects.hash(System.identityHashCode(source), ruleName, discriminator);
        }
    }
}

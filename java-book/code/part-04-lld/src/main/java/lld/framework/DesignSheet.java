package lld.framework;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

// ANCHOR: design-sheet
/** The written artefact of an LLD round: scope, entities, relations, APIs, patterns. */
public final class DesignSheet {
  private final String title;
  private final List<Requirement> requirements = new ArrayList<>();
  private final Set<String> entities = new TreeSet<>();
  private final List<Relation> relations = new ArrayList<>();
  private final List<String> apis = new ArrayList<>();
  private final Map<String, String> patterns = new LinkedHashMap<>();

  public DesignSheet(String title) {
    this.title = title;
  }

  public DesignSheet requirement(Requirement r) {
    requirements.add(r);
    return this;
  }

  public DesignSheet entity(String name) {
    entities.add(name);
    return this;
  }

  public DesignSheet relate(String from, Relation.Kind kind, String to, String label) {
    relations.add(new Relation(from, kind, to, label));
    return this;
  }

  public DesignSheet api(String signature) {
    apis.add(signature);
    return this;
  }

  public DesignSheet pattern(String name, String because) {
    patterns.put(name, because);
    return this;
  }

  public String title() {
    return title;
  }

  public Set<String> entities() {
    return Set.copyOf(entities);
  }

  public List<Relation> relations() {
    return List.copyOf(relations);
  }

  /** Returns the problems a reviewer would raise; empty means the sheet is internally consistent. */
  public List<String> validate() {
    List<String> problems = new ArrayList<>();
    if (requirements.stream().noneMatch(r -> r.kind() == Requirement.Kind.FUNCTIONAL)) {
      problems.add("no functional requirement");
    }
    for (Relation r : relations) {
      if (!entities.contains(r.from())) {
        problems.add("unknown entity in relation: " + r.from());
      }
      if (!entities.contains(r.to())) {
        problems.add("unknown entity in relation: " + r.to());
      }
    }
    for (String e : entities) {
      boolean used = relations.stream().anyMatch(r -> r.from().equals(e) || r.to().equals(e));
      if (!used) {
        problems.add("orphan entity: " + e);
      }
    }
    patterns.forEach((p, why) -> {
      if (why == null || why.isBlank()) {
        problems.add("pattern without reason: " + p);
      }
    });
    if (apis.isEmpty()) {
      problems.add("no API");
    }
    return problems;
  }

  public String render() {
    StringBuilder sb = new StringBuilder("# ").append(title).append('\n');
    sb.append("scope:\n");
    for (Requirement r : requirements) {
      sb.append("  [").append(r.kind()).append("] ").append(r.text()).append('\n');
    }
    sb.append("entities: ").append(entities).append('\n');
    sb.append("api:\n");
    for (String a : apis) {
      sb.append("  ").append(a).append('\n');
    }
    sb.append("patterns: ").append(patterns.keySet());
    return sb.toString();
  }
}
// ANCHOR_END: design-sheet

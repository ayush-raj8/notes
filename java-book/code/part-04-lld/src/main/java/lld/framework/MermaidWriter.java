package lld.framework;

// ANCHOR: mermaid-writer
/** Renders a sheet as a Mermaid classDiagram: entities first, then relations. */
public final class MermaidWriter {
  private MermaidWriter() {}

  public static String classDiagram(DesignSheet sheet) {
    StringBuilder sb = new StringBuilder("classDiagram\n");
    for (String e : sheet.entities()) {
      sb.append("  class ").append(e).append('\n');
    }
    for (Relation r : sheet.relations()) {
      sb.append("  ").append(r.toMermaid()).append('\n');
    }
    return sb.toString().stripTrailing();
  }
}
// ANCHOR_END: mermaid-writer

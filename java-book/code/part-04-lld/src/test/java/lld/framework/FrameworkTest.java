package lld.framework;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

// ANCHOR: framework-test
class FrameworkTest {
  @Test
  void stepsFitFortyFiveMinutes() {
    assertEquals(45, InterviewStep.totalMinutes());
    assertTrue(InterviewStep.EXTEND_AND_CONCURRENCY.next().isEmpty());
    assertEquals(InterviewStep.IDENTIFY_ENTITIES, InterviewStep.CLARIFY_REQUIREMENTS.next().orElseThrow());
  }

  @Test
  void requirementRejectsBlank() {
    assertThrows(IllegalArgumentException.class, () -> Requirement.functional(" "));
    assertEquals("A uses B", Requirement.functional(" A uses B ").text());
  }

  @Test
  void extractorReadsActorVerbObject() {
    var c = NounVerbExtractor.extract(List.of(Requirement.functional("customer places Order")));
    assertEquals(Set.of("Customer", "Order"), c.classes());
    assertEquals(Set.of("places"), c.methods());
    assertThrows(IllegalArgumentException.class,
        () -> NounVerbExtractor.extract(List.of(Requirement.functional("too short"))));
  }

  @Test
  void cafeSheetIsConsistentAndRendersDiagram() {
    DesignSheet sheet = FrameworkDemo.cafeSheet();
    assertEquals(List.of(), sheet.validate());
    String diagram = MermaidWriter.classDiagram(sheet);
    assertTrue(diagram.startsWith("classDiagram"));
    assertTrue(diagram.contains("Order *-- Drink : 1..*"));
  }

  @Test
  void validateFlagsOrphansAndUnknownEntities() {
    DesignSheet s = new DesignSheet("bad").entity("A").relate("A", Relation.Kind.ASSOCIATION, "Z", "x");
    List<String> p = s.validate();
    assertTrue(p.contains("no functional requirement"));
    assertTrue(p.contains("unknown entity in relation: Z"));
    assertTrue(p.contains("no API"));
  }

  @Test
  void sharedStateAudit() {
    var fields = List.of(new SharedStateAudit.StateField("C", "m", true, true),
        new SharedStateAudit.StateField("C", "k", false, true));
    assertEquals(List.of("C.m"), SharedStateAudit.needsGuard(fields));
    assertFalse(SharedStateAudit.needsGuard(fields).contains("C.k"));
  }
}
// ANCHOR_END: framework-test

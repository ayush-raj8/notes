package oop.uml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class UmlTest {
  private static final Path DIAGRAMS = Path.of("..", "..", "diagrams", "uml");

  private static List<String> lines(String relative) throws IOException {
    return Files.readAllLines(DIAGRAMS.resolve(relative));
  }

  /** Member lines (4-space indent) of one class block in a Mermaid file. */
  private static List<String> classMembers(List<String> mmd, String className) {
    List<String> out = new ArrayList<>();
    boolean in = false;
    for (String l : mmd) {
      if (l.strip().equals("class " + className + " {")) {
        in = true;
      } else if (in && l.strip().equals("}")) {
        break;
      } else if (in) {
        out.add(l.strip());
      }
    }
    return out;
  }

  @Test
  void dumpMatchesNotationAnnotations() {
    List<String> base = UmlDump.members(NotationDemo.Base.class);
    assertTrue(base.contains("-created: int$"));
    assertTrue(base.contains("#label: String"));
    assertTrue(base.contains("~tag: String"));
    assertTrue(base.contains("+area() double*"));
    assertTrue(base.contains("+created() int$"));
    assertTrue(UmlDump.classBlock(NotationDemo.Repository.class).contains("<<interface>>"));
  }

  @Test
  void classNotationDiagramMatchesBase() throws IOException {
    List<String> declared = classMembers(lines("class-notation.mmd"), "Base");
    declared.remove("<<abstract>>");
    assertEquals(UmlDump.members(NotationDemo.Base.class).stream().sorted().toList(), declared.stream().sorted().toList());
  }

  @Test
  void compositionSolutionDiagramMatchesReflection() throws IOException {
    List<String> mmd = lines("solutions/composition.mmd");
    assertEquals(
        UmlDump.members(CompositionDemo.House.class).stream().sorted().toList(),
        classMembers(mmd, "House").stream().sorted().toList());
    assertEquals(
        UmlDump.members(CompositionDemo.Room.class).stream().sorted().toList(),
        classMembers(mmd, "Room").stream().sorted().toList());
  }

  @Test
  void stateDiagramEqualsCode() throws IOException {
    Pattern edge = Pattern.compile("(\\w+) --> (\\w+) : (\\w+)");
    List<String> declared = new ArrayList<>();
    for (String l : lines("state-order.mmd")) {
      Matcher m = edge.matcher(l.strip());
      if (m.matches()) {
        declared.add(m.group(1) + "/" + m.group(3));
        assertEquals(
            StateDemo.State.valueOf(m.group(2)),
            StateDemo.next(StateDemo.State.valueOf(m.group(1)), StateDemo.Event.valueOf(m.group(3))));
      }
    }
    for (StateDemo.State s : StateDemo.State.values()) {
      for (StateDemo.Event e : StateDemo.Event.values()) {
        if (!declared.contains(s + "/" + e)) {
          assertThrows(IllegalStateException.class, () -> StateDemo.next(s, e), s + "/" + e);
        }
      }
    }
  }

  private static List<String> arrows(String relative) throws IOException {
    Pattern arrow = Pattern.compile("(\\w+)->>(\\w+): (.+)");
    List<String> out = new ArrayList<>();
    for (String l : lines(relative)) {
      Matcher m = arrow.matcher(l.strip());
      if (m.matches()) out.add(m.group(1) + " -> " + m.group(2) + ": " + m.group(3));
    }
    return out;
  }

  @Test
  void sequenceDiagramMatchesFailureAndSuccessLogs() throws IOException {
    SequenceDemo.run(false);
    assertEquals(arrows("solutions/sequence-failure.mmd"), SequenceDemo.calls);
    SequenceDemo.run(true);
    assertEquals(
        List.of(
            "Client -> OrderService: place(A1)",
            "OrderService -> Inventory: reserve(A1)",
            "OrderService -> Payment: charge(50)",
            "OrderService -> Notifier: send(confirmed)"),
        SequenceDemo.calls);
    // the full diagram lists both branches of the alt fragment
    assertTrue(arrows("sequence-order.mmd").containsAll(SequenceDemo.calls));
  }

  @Test
  void relationshipDiagramUsesEveryOperator() throws IOException {
    String all = String.join("\n", lines("relationships.mmd"));
    for (String op : List.of("<|--", "<|..", "o--", "*--", "-->", "<-->", "..>")) {
      assertTrue(all.contains(op), op);
    }
  }

  @Test
  void everyPlantUmlSourceIsWrapped() throws IOException {
    try (Stream<Path> files = Files.walk(DIAGRAMS)) {
      for (Path p : files.filter(f -> f.toString().endsWith(".puml")).toList()) {
        List<String> l = Files.readAllLines(p);
        assertTrue(l.get(0).startsWith("@startuml"), p.toString());
        assertEquals("@enduml", l.get(l.size() - 1), p.toString());
        Path mmd = Path.of(p.toString().replace(".puml", ".mmd"));
        assertTrue(Files.exists(mmd), "missing Mermaid twin for " + p);
      }
    }
  }

  @Test
  void aggregationPartsOutliveTheWhole() {
    AggregationDemo.Player p = new AggregationDemo.Player("x");
    AggregationDemo.Team a = new AggregationDemo.Team("a");
    AggregationDemo.Team b = new AggregationDemo.Team("b");
    a.add(p);
    b.add(p);
    a.disband();
    assertEquals(0, a.players().size());
    assertTrue(b.players().contains(p));
  }

  @Test
  void compositionPartsEndWithTheWholeAndCannotBeEmpty() {
    CompositionDemo.House h = new CompositionDemo.House("a", "b");
    assertEquals(List.of("a", "b"), h.roomNames());
    h.close();
    assertEquals(List.of(), h.roomNames());
    assertThrows(IllegalArgumentException.class, CompositionDemo.House::new);
    assertThrows(UnsupportedOperationException.class, () -> h.roomNames().add("x"));
  }

  @Test
  void navigabilityIsOneWayUnlessWired() {
    AssociationDemo.Author a = new AssociationDemo.Author("A");
    AssociationDemo.Book b = new AssociationDemo.Book("B");
    assertNull(b.author());
    a.addBook(b);
    assertEquals("A", b.author().name());
    assertEquals(1, a.books().size());
    assertEquals(
        0,
        java.util.Arrays.stream(AssociationDemo.Customer.class.getDeclaredFields())
            .filter(f -> f.getType() == AssociationDemo.Order.class)
            .count());
  }

  @Test
  void dependencyHasNoField() {
    assertEquals(0, DependencyDemo.formatterFields(DependencyDemo.Report.class));
    assertEquals(1, DependencyDemo.formatterFields(DependencyDemo.Holder.class));
  }

  @Test
  void multiplicityIsEnforcedByTheConstructor() {
    MultiplicityDemo.Customer c = new MultiplicityDemo.Customer("c");
    MultiplicityDemo.Line l = new MultiplicityDemo.Line("s", 1);
    assertThrows(IllegalArgumentException.class, () -> new MultiplicityDemo.Order(c, List.of(), null));
    assertThrows(NullPointerException.class, () -> new MultiplicityDemo.Order(null, List.of(l), null));
    assertFalse(new MultiplicityDemo.Order(c, List.of(l), null).coupon().isPresent());
    assertTrue(new MultiplicityDemo.Order(c, List.of(l), new MultiplicityDemo.Coupon("x")).coupon().isPresent());
  }

  @Test
  void generalizationAndRealization() {
    assertEquals(InheritRealize.Animal.class, InheritRealize.Dog.class.getSuperclass());
    assertEquals(2, InheritRealize.ArrayStack.class.getInterfaces().length);
    assertEquals(2, InheritRealize.Swimmer.class.getInterfaces().length);
  }

  @Test
  void activityAndUseCase() {
    assertEquals(List.of("validate cart", "reject: empty cart"), ActivityDemo.checkout(List.of()));
    assertEquals(
        List.of("validate cart", "charge card", "reserve stock", "ship"), ActivityDemo.checkout(List.of("A1")));
    UseCaseDemo.Shop shop = new UseCaseDemo.Shop();
    assertEquals("ordered A1 for 90", shop.placeOrder("A1", "SAVE10"));
    assertEquals(List.of("placeOrder(A1)", "applyCoupon(SAVE10)", "pay()"), shop.trace);
  }
}

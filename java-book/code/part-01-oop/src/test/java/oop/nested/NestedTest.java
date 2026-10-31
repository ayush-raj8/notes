package oop.nested;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Test;

class NestedTest {
  @Test
  void staticNestedNeedsNoOuterInstance() {
    StaticNestedDemo.Counter c = new StaticNestedDemo.Counter();
    assertEquals(1, c.next());
    assertEquals(2, c.next());
  }

  @Test
  void innerSharesOuterState() {
    InnerDemo a = new InnerDemo("A");
    InnerDemo b = new InnerDemo("B");
    assertEquals("A#1", a.issue().toString());
    assertEquals("A#2", a.new Ticket().toString());
    assertEquals("B#1", b.issue().toString());
  }

  @Test
  void shadowingPicksNearestAndQualifiedNames() {
    ShadowDemo outer = new ShadowDemo();
    assertEquals("3 2 1", outer.new Inner().show(3));
  }

  @Test
  void localClassCapturesEffectivelyFinal() {
    assertEquals("hi Ann;hi Ann;", LocalClassDemo.greeter("hi", 2));
  }

  @Test
  void localRecordWorks() {
    assertEquals("abcd:4", LocalRecordDemo.top(List.of("ab", "abcd", "abc")));
  }

  @Test
  void binaryNamesFollowOuterDollarPattern() {
    assertEquals("oop.nested.NamesDemo$Sn", NamesDemo.Sn.class.getName());
    assertEquals("oop.nested.NamesDemo$In", NamesDemo.In.class.getName());
  }

  @Test
  void innerHoldsOuterReferenceStaticNestedDoesNot() throws Exception {
    LeakTrap host = new LeakTrap("h");
    assertTrue(LeakTrap.holdsOuter(host.inner(), host));
    assertFalse(LeakTrap.holdsOuter(host.safe(), host));
    assertEquals(List.of("this$0 (synthetic)"), LeakTrap.fieldNames(LeakTrap.InnerLabel.class));
  }

  @Test
  void interfaceNestedTypesAreStaticAndPublic() {
    InterfaceNested.Config c = new InterfaceNested.Config();
    assertEquals(3, c.limit);
    assertEquals(1, InterfaceNested.Mode.SAFE.ordinal());
    assertEquals("Pair[a=x, b=y]", new InterfaceNested.Pair("x", "y").toString());
  }

  @Test
  void nestedKindsRun() {
    assertEquals("(1,2) 8 10", NestedKinds.run());
  }

  @Test
  void whichKindIteratesInReverseInsertionOrder() {
    WhichKind k = new WhichKind();
    k.add("a");
    k.add("b");
    List<String> seen = new ArrayList<>();
    for (String s : k) {
      seen.add(s);
    }
    assertEquals(List.of("b", "a"), seen);
    assertEquals("b/a", k.joinWith("/"));
    var it = k.iterator();
    it.next();
    it.next();
    assertThrows(NoSuchElementException.class, it::next);
  }

  @Test
  void anonymousClassBinaryNameIsNumbered() {
    Object o = new Object() {};
    assertEquals("oop.nested.NestedTest$1", o.getClass().getName());
    assertNotEquals("", o.getClass().getName());
  }
}

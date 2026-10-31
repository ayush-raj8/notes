package oop.override;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class OverrideTest {
  @Test
  void overrideChoosesBodyByObjectClassAndSuperCallsUp() {
    Greeter g = new LoudGreeter();
    assertEquals("HELLO ANN!", g.greet("ann"));
    assertEquals("A>B>C", new ChainC().path());
    assertEquals("A>B", new ChainB().path());
  }

  @Test
  void covariantReturnNeedsNoCast() {
    Memo copy = new Memo().copy();
    assertEquals("memo-only", copy.memoOnly());
    Doc d = new Memo();
    assertInstanceOf(Memo.class, d.copy());
  }

  @Test
  void accessMayWiden() throws Exception {
    assertTrue(Modifier.isProtected(WideBase.class.getDeclaredMethod("id").getModifiers()));
    assertTrue(Modifier.isPublic(WideOpen.class.getDeclaredMethod("id").getModifiers()));
  }

  @Test
  void checkedExceptionsMayNarrowOrDisappear() {
    assertThrows(FileNotFoundException.class, () -> new NarrowSource().read());
    assertThrows(IOException.class, () -> new Source().read());
    assertThrows(IllegalStateException.class, () -> new UncheckedSource().read());
    new QuietSource().read();
  }

  @Test
  void staticIsHiddenFieldIsHiddenInstanceIsOverridden() {
    HideParent p = new HideChild();
    assertEquals("HideChild.me", p.me());
    assertEquals("HideParent.who", HideParent.who());
    assertEquals("HideChild.who", HideChild.who());
    FieldParent f = new FieldChild();
    assertEquals("parent-field", f.name);
    assertEquals("child-field / parent-field", f.getName());
  }

  @Test
  void privateAndFinalAreNotOverridden() {
    SealedChild c = new SealedChild();
    assertEquals("parent-private|parent-final", c.run());
    assertEquals("child-secret", c.secret());
  }

  @Test
  void bridgeMethodsExist() {
    boolean holderBridge = false;
    for (Method m : TextHolder.class.getDeclaredMethods()) {
      holderBridge |= m.isBridge() && m.getReturnType() == Object.class;
    }
    assertTrue(holderBridge);
    boolean compareBridge = false;
    for (Method m : Label.class.getDeclaredMethods()) {
      compareBridge |= m.isBridge() && m.getParameterTypes()[0] == Object.class;
    }
    assertTrue(compareBridge);
  }

  @Test
  @SuppressWarnings({"rawtypes", "unchecked"})
  void bridgeThrowsClassCast() {
    Comparable raw = new Label("a");
    assertThrows(ClassCastException.class, () -> raw.compareTo("x"));
  }

  @Test
  void defaultDiamondAndClassWins() {
    assertEquals("walk+swim", new Duck().move());
    assertEquals("crawl", new Newt().move());
  }

  @Test
  void constructorsAreNotInherited() {
    assertEquals(1, Vehicle.class.getDeclaredConstructors().length);
    assertEquals(2, Car.class.getDeclaredConstructors().length);
  }

  @Test
  void constructorHookSeesDefaultsButFactoryDoesNot() {
    java.io.PrintStream old = System.out;
    java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
    System.setOut(new java.io.PrintStream(buf, true));
    try {
      new TrapDerived();
      SafeDerived.create("ready");
    } finally {
      System.setOut(old);
    }
    String out = buf.toString().replace("\r", "");
    assertTrue(out.contains("derived hook label=null size=0"));
    assertTrue(out.contains("derived hook label=ready state=base-ready"));
    assertFalse(out.contains("label=ready size"));
  }

  @Test
  void abstractMethodsAreImplementedAndMayBeReAbstracted() {
    assertEquals("Square area=9.0", new Square(3).describe());
    assertEquals("Rect area=10.0", new Rect(2, 5).describe());
    Runner r = new FastRunner();
    assertEquals("fast-run", r.run());
  }
}

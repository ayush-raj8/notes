package oop.init;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/**
 * Runs each demo {@code main} in a fresh class loader so static initialization
 * always happens from scratch, regardless of test order.
 */
class InitTest {
  private static String run(String mainClass) throws Exception {
    URL classes = InitTrace.class.getProtectionDomain().getCodeSource().getLocation();
    PrintStream original = System.out;
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    try (URLClassLoader loader = new URLClassLoader(new URL[] {classes}, ClassLoader.getPlatformClassLoader())) {
      System.setOut(new PrintStream(buffer, true));
      Class<?> type = Class.forName(mainClass, true, loader);
      Method main = type.getMethod("main", String[].class);
      main.invoke(null, (Object) new String[0]);
    } finally {
      System.setOut(original);
    }
    return buffer.toString().replace("\r\n", "\n");
  }

  private static String lines(String... lines) {
    return String.join("\n", lines) + "\n";
  }

  @Test
  void staticBlockRunsOnceOnFirstUse() throws Exception {
    assertEquals(
        lines("main start", "LazyConfig static block", "LazyConfig ctor", "LazyConfig ctor", "loads=1"),
        run("oop.init.StaticBlockDemo"));
  }

  @Test
  void fieldsBlocksConstructorInTextualOrder() throws Exception {
    assertEquals(
        lines(
            "static field s1", "static block", "static field s2",
            "instance field f1", "instance block", "instance field f2", "constructor",
            "-- second object --",
            "instance field f1", "instance block", "instance field f2", "constructor"),
        run("oop.init.InstanceOrderDemo"));
  }

  @Test
  void parentChildOrder() throws Exception {
    assertEquals(
        lines(
            "main start",
            "Parent static field", "Parent static block",
            "Child static field", "Child static block",
            "Parent instance field", "Parent instance block", "Parent constructor",
            "Child instance field", "Child instance block", "Child constructor",
            "-- second object --",
            "Parent instance field", "Parent instance block", "Parent constructor",
            "Child instance field", "Child instance block", "Child constructor"),
        run("oop.init.ParentChildDemo"));
  }

  @Test
  void instanceBlockRunsOnceWithThisChain() throws Exception {
    assertEquals(lines("instance block", "Multi(int)", "Multi()"), run("oop.init.InitBlockThisChain"));
  }

  @Test
  void overriddenCallInParentConstructorSeesDefault() throws Exception {
    assertEquals(
        lines("Base ctor sees: null", "Loud ctor sees: loud"),
        run("oop.init.OverrideInCtorGotcha"));
  }

  @Test
  void forwardReferenceEdge() throws Exception {
    assertEquals(
        lines("viaMethod=0", "viaThis=1", "later=5"), run("oop.init.ForwardReferenceEdge"));
  }

  @Test
  void compileTimeConstantDoesNotTriggerInit() throws Exception {
    assertEquals(
        lines("K=10", "S=s", "Constants static block", "NOT_CONSTANT=11"),
        run("oop.init.InitTriggerDemo"));
  }

  @Test
  void failedStaticInitThenNoClassDefFound() throws Exception {
    assertEquals(
        lines(
            "first: ExceptionInInitializerError, cause NumberFormatException",
            "second: NoClassDefFoundError, Could not initialize class oop.init.FailingStatic"),
        run("oop.init.StaticInitFailureDemo"));
  }

  @Test
  void predictBank() throws Exception {
    assertEquals(lines("S", "I", "C", "I", "C"), run("oop.init.exam.PredictInitOrder"));
    assertEquals(lines("static", "main"), run("oop.init.exam.PredictStaticMain"));
    assertEquals(lines("side=0", "side=4"), run("oop.init.exam.PredictOverrideCtor"));
    assertEquals(lines("Sup static", "v=1"), run("oop.init.exam.PredictSubViaSuper"));
    assertEquals(lines("116"), run("oop.init.exam.PredictThisChainBlock"));
    assertEquals(lines("3"), run("oop.init.exam.PredictStaticCounter"));
    assertEquals(lines("5", "K init", "6"), run("oop.init.exam.PredictConstantTrigger"));
  }

  @Test
  void compileOkForwardAssignmentAndThis() throws Exception {
    assertEquals(
        lines("early=1", "viaThis=1, later=7"), run("oop.init.exam.CompileForwardOk"));
  }
}

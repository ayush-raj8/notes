package oop.abstracts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class AbstractsTest {
  @Test
  void encapsulationKeepsInvariant() {
    SafeAccount a = new SafeAccount(500);
    a.deposit(250);
    assertFalse(a.withdraw(1000));
    assertTrue(a.withdraw(300));
    assertEquals(450, a.balance());
    assertThrows(IllegalArgumentException.class, () -> a.deposit(-5));
    assertThrows(IllegalArgumentException.class, () -> new SafeAccount(-1));
  }

  @Test
  void subclassInheritsAndOverrides() {
    Truck t = new Truck("T1", 900);
    t.drive(40);
    assertEquals(40, t.odometer());
    Vehicle v = t;
    assertEquals("truck T1 load=900", v.describe());
  }

  @Test
  void abstractClassAnonymousSubclassAndTemplateMethod() {
    Shape s = new Square(3);
    assertEquals("square area=9.0", s.report());
    Shape unit = new Shape("unit") {
      @Override
      double area() {
        return 1;
      }
    };
    assertEquals("unit area=1.0", unit.report());
  }

  @Test
  void interfaceMembers() {
    assertEquals("Hi Anna", new Anna().greet());
    assertEquals("Hi Bo", Greeter.of("Bo").greet());
    assertEquals(8, Greeter.MAX_NAME);
  }

  @Test
  void multipleInterfacesResolveDefaultConflict() {
    assertEquals("readable+writable", new File2().id());
    assertTrue(Readable2.class.isAssignableFrom(File2.class));
  }

  @Test
  void sealedIntrospectionAndExhaustiveSwitch() {
    assertTrue(Figure.class.isSealed());
    assertEquals(
        List.of("Disc", "Box", "Blob"),
        java.util.Arrays.stream(Figure.class.getPermittedSubclasses()).map(Class::getSimpleName).toList());
    assertEquals("disc r=2.0", SealedShapes.describe(new Disc(2)));
    assertEquals("blob BlobWithHole", SealedShapes.describe(new BlobWithHole()));
    assertTrue(Payment.class.isSealed());
    assertFalse(Card.class.isSealed());
    assertTrue(Cash.class.isSealed());
  }

  @Test
  void abstractConstructorSeesDefaultField() {
    assertEquals("T-ctor,init sees null,", new Impl().log);
  }

  @Test
  void functionalInterfaceIsLambdaTarget() {
    Op plus = (a, b) -> a + b;
    assertEquals(8, plus.twice().apply(2, 3));
    assertEquals(2, Op.add().apply(1, 1));
  }
}

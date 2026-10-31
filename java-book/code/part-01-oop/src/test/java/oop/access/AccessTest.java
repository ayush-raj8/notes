package oop.access;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import oop.access.other.Consts;
import oop.access.other.OtherPackageDemo;
import oop.access.other.OtherSubclass;
import org.junit.jupiter.api.Test;

class AccessTest {
  @Test
  void ownClassSeesAllFour() {
    assertEquals("public,protected,package-private,private", new AccessLevels().inside());
  }

  @Test
  void samePackageSeesThreeLevels() {
    assertEquals("public,protected,package-private", SamePackageDemo.visible());
  }

  @Test
  void otherPackageSeesPublicOnly() {
    assertEquals("public", OtherPackageDemo.visible());
  }

  @Test
  void otherPackageSubclassSeesProtectedThroughOwnType() {
    OtherSubclass s = new OtherSubclass();
    assertEquals("public,protected", s.viaInheritance());
    assertEquals("protected", s.viaSameType(new OtherSubclass()));
  }

  @Test
  void staticCounterIsSharedInstanceIdIsNot() {
    int before = Counter.created();
    Counter a = new Counter();
    Counter b = new Counter();
    assertEquals(before + 1, a.id());
    assertEquals(before + 2, b.id());
    assertEquals(before + 2, Counter.created());
  }

  @Test
  void staticCallThroughNullWorksInstanceCallThrows() {
    Counter c = null;
    assertEquals(Counter.created(), c.created());
    assertThrows(NullPointerException.class, () -> {
      Counter nullRef = null;
      nullRef.id();
    });
  }

  @Test
  void finalDemoAddsId() {
    assertEquals(15, new FinalDemo(10).add(5));
    assertEquals(3, FinalDemo.MAX);
  }

  @Test
  void staticImportedMembersMatchQualifiedOnes() {
    assertEquals(8, Consts.twice(4));
    assertEquals(0.5, Consts.RATE);
  }
}

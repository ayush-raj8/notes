package oop.ctors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class CtorTest {
  @Test
  void defaultConstructorExistsWhenNoneDeclared() {
    DefaultBox box = new DefaultBox();
    box.setSize(2);
    assertEquals(2, box.size());
  }

  @Test
  void copyConstructorIndependentBalance() {
    Account a = new Account("A", 10);
    Account b = new Account(a);
    a.deposit(5);
    assertEquals(15, a.balance());
    assertEquals(10, b.balance());
    assertNotSame(a, b);
  }

  @Test
  void privateCtorViaFactory() {
    assertEquals(0, Account.zero("Z").balance());
  }

  @Test
  void thisChainDelegates() {
    assertEquals("(0,0)", new ThisChain().toString());
    assertEquals("(3,3)", new ThisChain(3).toString());
  }

  @Test
  void superChainSetsParentFields() {
    assertEquals("Rex/Lab", new Dog("Rex", "Lab").toString());
  }

  @Test
  void explicitNoArgRestored() {
    assertEquals(0, new OnlyParamFixed().n());
  }

  @Test
  void subclassCtorCallsSuper() {
    assertEquals(3, new FixedChild(3).n());
  }
}

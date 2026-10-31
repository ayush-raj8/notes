package oop.compare;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CompareTest {
  @Test
  void overrideIsChosenByObject() {
    CmpBase b = new CmpDerived();
    assertEquals("Derived.who", b.who());
  }

  @Test
  void staticAndFieldHidingFollowReferenceType() {
    CmpBase b = new CmpDerived();
    assertEquals("Base.kind", b.kind());
    assertEquals("Base.tag", b.tag);
    assertEquals("Derived.tag", ((CmpDerived) b).tag);
  }

  @Test
  void overloadIsChosenByReferenceType() {
    CmpBase b = new CmpDerived();
    CmpDerived d = new CmpDerived();
    assertEquals("Base.pick(Object)", b.pick("s"));
    assertEquals("Derived.pick(String)", d.pick("s"));
  }

  @Test
  void shadowingParameterNeedsThis() {
    assertEquals("p | Derived.tag | Base.tag", new CmpDerived().shadow("p"));
  }

  @Test
  void wrongEqualsParameterIsAnOverloadOnly() {
    CmpPoint a = new CmpPoint(1);
    Object o = new CmpPoint(1);
    assertTrue(a.equals(new CmpPoint(1)));
    assertFalse(a.equals(o));
    assertFalse(List.of(a).contains(new CmpPoint(1)));
    assertTrue(List.of(new CmpPointOk(1)).contains(new CmpPointOk(1)));
  }

  @Test
  void fieldsAreNotVirtual() {
    CmpAnimal a = new CmpCat();
    assertEquals("animal", a.direct());
    assertEquals("cat", a.viaGetter());
  }
}

package oop.compare;

// ANCHOR: compare-field-hide
class CmpAnimal {
  String name = "animal";

  String getName() {
    return name;
  }

  String direct() {
    return name; // always CmpAnimal.name
  }

  String viaGetter() {
    return getName(); // virtual call
  }
}

class CmpCat extends CmpAnimal {
  String name = "cat"; // hides, does not replace

  @Override
  String getName() {
    return name; // CmpCat.name
  }
}

/** Fields are not virtual: the reference type picks the field. Methods are virtual. */
public final class CompareFieldHide {
  public static void main(String[] args) {
    CmpAnimal a = new CmpCat();
    CmpCat c = new CmpCat();
    System.out.println(a.name + " " + c.name + " " + ((CmpAnimal) c).name);
    System.out.println(a.direct() + " " + a.viaGetter());
    System.out.println(c.direct() + " " + c.viaGetter());
  }
}
// ANCHOR_END: compare-field-hide

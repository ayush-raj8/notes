package oop.override;

// ANCHOR: override-field-hiding
class FieldParent {
  String name = "parent-field";

  String getName() {
    return name;
  }
}

class FieldChild extends FieldParent {
  String name = "child-field";

  @Override
  String getName() {
    return name + " / " + super.name;
  }
}

/** Fields are never overridden: a same-named field hides the parent's, chosen by reference type. */
public final class OverrideFieldHiding {
  public static void main(String[] args) {
    FieldParent p = new FieldChild();
    System.out.println(p.name);
    System.out.println(((FieldChild) p).name);
    System.out.println(p.getName());
  }
}
// ANCHOR_END: override-field-hiding

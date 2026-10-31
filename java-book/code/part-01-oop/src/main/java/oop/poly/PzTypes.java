package oop.poly;

// ANCHOR: pz-types
class PzAnimal {
  String sound() {
    return "animal";
  }
}

class PzDog extends PzAnimal {
  @Override
  String sound() {
    return "woof";
  }

  String fetch() {
    return "ball";
  }
}

class PzCat extends PzAnimal {
  @Override
  String sound() {
    return "meow";
  }
}
// ANCHOR_END: pz-types

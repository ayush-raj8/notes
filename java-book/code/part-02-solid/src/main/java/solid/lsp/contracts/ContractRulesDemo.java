package solid.lsp.contracts;

import java.util.ArrayList;
import java.util.List;

public final class ContractRulesDemo {
  // ANCHOR: covariant
  static class Animal {
    Animal mate() {
      return new Animal();
    }
  }

  static class Dog extends Animal {
    @Override
    Dog mate() { // stronger postcondition: covariant return is allowed
      return new Dog();
    }
  }
  // ANCHOR_END: covariant

  // ANCHOR: weaker-pre
  static class Parser {
    int parse(String s) {
      if (s.isEmpty()) {
        throw new IllegalArgumentException("empty");
      }
      return Integer.parseInt(s);
    }
  }

  static class LenientParser extends Parser {
    @Override
    int parse(String s) { // weaker precondition: accepts more than the parent
      return s.isEmpty() ? 0 : super.parse(s);
    }
  }
  // ANCHOR_END: weaker-pre

  // ANCHOR: bag
  static class Bag {
    final List<String> items = new ArrayList<>();

    void add(String s) {
      items.add(s);
    }
  }

  static class FrozenBag extends Bag {
    @Override
    void add(String s) { // compiles, but clients of Bag now meet a surprise
      throw new UnsupportedOperationException("frozen");
    }
  }

  static void fill(Bag b) {
    b.add("x");
    b.add("y");
  }
  // ANCHOR_END: bag

  public static void main(String[] args) {
    // ANCHOR: contracts-main
    Animal a = new Dog();
    System.out.println("covariant: " + a.mate().getClass().getSimpleName());

    Parser p = new LenientParser();
    System.out.println("lenient: " + p.parse("") + " " + p.parse("42"));

    Bag ok = new Bag();
    fill(ok);
    System.out.println("bag: " + ok.items);
    try {
      fill(new FrozenBag());
    } catch (UnsupportedOperationException e) {
      System.out.println("frozen bag: " + e.getMessage());
    }
    try {
      List.of(1).add(2);
    } catch (UnsupportedOperationException e) {
      System.out.println("List.of add: " + e.getClass().getSimpleName());
    }
    // ANCHOR_END: contracts-main
  }
}

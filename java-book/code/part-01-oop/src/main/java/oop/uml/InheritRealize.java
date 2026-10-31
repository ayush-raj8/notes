package oop.uml;

import java.util.ArrayDeque;
import java.util.Deque;

// ANCHOR: inherit-realize
/** Generalization (--|>) = extends. Realization (..|>) = implements. One superclass, many interfaces. */
public final class InheritRealize {
  private InheritRealize() {}

  static class Animal {
    String sound() {
      return "...";
    }
  }

  static final class Dog extends Animal { // Dog --|> Animal
    @Override
    String sound() {
      return "woof";
    }
  }

  interface Stack<T> {
    void push(T x);

    T pop();
  }

  interface Sized {
    int size();
  }

  static final class ArrayStack<T> implements Stack<T>, Sized { // ..|> Stack, ..|> Sized
    private final Deque<T> items = new ArrayDeque<>();

    @Override
    public void push(T x) {
      items.push(x);
    }

    @Override
    public T pop() {
      return items.pop();
    }

    @Override
    public int size() {
      return items.size();
    }
  }

  interface Walker {
    default String move() {
      return "walk";
    }
  }

  interface Swimmer extends Walker, Sized { // interface --|> interface (several allowed)
  }

  public static void main(String[] args) {
    Animal a = new Dog();
    System.out.println("Dog is an Animal: " + (a instanceof Animal) + ", sound=" + a.sound());
    ArrayStack<String> s = new ArrayStack<>();
    s.push("a");
    s.push("b");
    System.out.println("pop=" + s.pop() + " size=" + s.size());
    System.out.println("Stack realized: " + (s instanceof Stack) + ", Sized realized: " + (s instanceof Sized));
    System.out.println("Swimmer extends: " + java.util.Arrays.stream(Swimmer.class.getInterfaces())
        .map(Class::getSimpleName).sorted().toList());
    System.out.println("Dog superclass: " + Dog.class.getSuperclass().getSimpleName());
  }
}
// ANCHOR_END: inherit-realize

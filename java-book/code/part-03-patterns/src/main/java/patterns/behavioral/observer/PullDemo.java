package patterns.behavioral.observer;

public final class PullDemo {
  // ANCHOR: pull-demo
  public static void main(String[] args) {
    Thermometer t = new Thermometer();
    t.watch(x -> System.out.println("celsius=" + x.celsius()));
    t.watch(x -> System.out.println("fahrenheit=" + (x.celsius() * 9 / 5 + 32)));
    t.watch(x -> System.out.println("changes=" + x.changes()));
    t.set(20);
    t.set(100);
  }
  // ANCHOR_END: pull-demo
}

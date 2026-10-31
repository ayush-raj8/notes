package patterns.structural.flyweight;

// ANCHOR: flyweight-demo
public final class FlyweightDemo {
  public static void main(String[] args) {
    TreeFactory.reset();
    Forest f = new Forest();
    f.plant(1, 1, "oak", "green");
    f.plant(5, 2, "oak", "green");
    f.plant(9, 4, "pine", "dark");
    f.plant(3, 7, "oak", "green");
    f.drawAll().forEach(System.out::println);
    System.out.println(f.size() + " trees, " + TreeFactory.created() + " shared types");
    System.out.println(TreeFactory.get("oak", "green") == TreeFactory.get("oak", "green"));
  }
}
// ANCHOR_END: flyweight-demo

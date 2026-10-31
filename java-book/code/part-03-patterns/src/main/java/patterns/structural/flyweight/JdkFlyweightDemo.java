package patterns.structural.flyweight;

// ANCHOR: jdk-flyweight-demo
public final class JdkFlyweightDemo {
  public static void main(String[] args) {
    System.out.println(Integer.valueOf(127) == Integer.valueOf(127));
    System.out.println(Integer.valueOf(128) == Integer.valueOf(128));
    System.out.println(Boolean.valueOf("true") == Boolean.TRUE);
    System.out.println(Character.valueOf('a') == Character.valueOf('a'));
    String lit = "java";
    System.out.println(lit == "java");
    System.out.println(lit == new String("java"));
    System.out.println(lit == new String("java").intern());
  }
}
// ANCHOR_END: jdk-flyweight-demo

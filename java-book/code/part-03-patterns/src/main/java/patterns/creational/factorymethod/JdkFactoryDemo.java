package patterns.creational.factorymethod;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// ANCHOR: jdk-factory-demo
public final class JdkFactoryDemo {
  public static void main(String[] args) {
    System.out.println(Integer.valueOf(127) == Integer.valueOf(127));
    System.out.println(NumberFormat.getInstance(Locale.US).format(1234567.891));
    System.out.println(List.of(1, 2, 3).iterator().next());
    System.out.println(java.nio.file.Path.of("a", "b").getNameCount());
    System.out.println(java.util.Calendar.getInstance(Locale.US).getClass().getSimpleName());
  }
}
// ANCHOR_END: jdk-factory-demo

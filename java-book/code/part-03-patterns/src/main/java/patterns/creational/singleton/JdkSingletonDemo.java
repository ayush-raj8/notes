package patterns.creational.singleton;

import java.util.Collections;
import java.util.logging.LogManager;

// ANCHOR: jdk-singleton-demo
public final class JdkSingletonDemo {
  public static void main(String[] args) {
    System.out.println(Runtime.getRuntime() == Runtime.getRuntime());
    System.out.println(LogManager.getLogManager() == LogManager.getLogManager());
    System.out.println(Collections.emptyList() == Collections.emptyList());
  }
}
// ANCHOR_END: jdk-singleton-demo

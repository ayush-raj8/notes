package patterns.behavioral.chain;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/** JDK chains: class-loader delegation, logger parents, and catch clauses. */
public final class JdkChainDemo {
  // ANCHOR: jdk-demo
  public static void main(String[] args) {
    ClassLoader cl = ClassLoader.getSystemClassLoader();
    while (cl != null) {
      System.out.println("loader " + cl.getName());
      cl = cl.getParent();
    }
    System.out.println("loader <bootstrap>");

    Logger parent = Logger.getLogger("app");
    parent.setUseParentHandlers(false);
    parent.addHandler(
        new Handler() {
          @Override
          public void publish(LogRecord r) {
            System.out.println("app handler got: " + r.getMessage() + " from " + r.getLoggerName());
          }

          @Override
          public void flush() {}

          @Override
          public void close() {}
        });
    Logger child = Logger.getLogger("app.db");
    System.out.println(child.getParent() == parent);
    child.log(Level.INFO, "connected");
    child.setLevel(Level.WARNING);
    child.log(Level.INFO, "dropped by child level");
    child.log(Level.WARNING, "slow query");
  }
  // ANCHOR_END: jdk-demo
}

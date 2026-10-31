package patterns.structural.bridge;

import java.sql.DriverManager;
import java.sql.SQLException;

// ANCHOR: jdk-bridge-demo
/** JDBC: java.sql is the abstraction; each database ships its own Driver (the implementor). */
public final class JdkBridgeDemo {
  public static void main(String[] args) {
    System.out.println(DriverManager.getDrivers().hasMoreElements());
    try {
      DriverManager.getConnection("jdbc:none:demo");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
// ANCHOR_END: jdk-bridge-demo

package patterns.structural.decorator;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// ANCHOR: jdk-decorator-demo
public final class JdkDecoratorDemo {
  public static void main(String[] args) throws IOException {
    // Reader decorated with buffering + readLine()
    try (BufferedReader r = new BufferedReader(new StringReader("one\ntwo"))) {
      System.out.println(r.readLine() + "," + r.readLine() + "," + r.readLine());
    }

    // OutputStream decorated with typed writes
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    try (DataOutputStream out = new DataOutputStream(bytes)) {
      out.writeInt(258);
    }
    System.out.println(bytes.size());

    // Collection decorated to be read-only (a live view)
    List<String> base = new ArrayList<>(List.of("a"));
    List<String> readOnly = Collections.unmodifiableList(base);
    base.add("b");
    System.out.println(readOnly);
    try {
      readOnly.add("c");
    } catch (UnsupportedOperationException e) {
      System.out.println("UOE");
    }
  }
}
// ANCHOR_END: jdk-decorator-demo

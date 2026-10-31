package patterns.structural.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

// ANCHOR: jdk-adapter-demo
public final class JdkAdapterDemo {
  public static void main(String[] args) throws Exception {
    // array -> List
    String[] arr = {"a", "b"};
    List<String> view = Arrays.asList(arr);
    view.set(0, "z");
    System.out.println(arr[0]);

    // byte stream -> character stream
    InputStreamReader reader =
        new InputStreamReader(
            new ByteArrayInputStream("hi".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    System.out.println((char) reader.read() + "" + (char) reader.read());

    // Collection -> legacy Enumeration
    Enumeration<String> e = Collections.enumeration(List.of("x", "y"));
    System.out.println(e.nextElement() + e.nextElement() + e.hasMoreElements());

    // Runnable -> Callable
    Callable<String> c = Executors.callable(() -> {}, "done");
    System.out.println(c.call());
  }
}
// ANCHOR_END: jdk-adapter-demo

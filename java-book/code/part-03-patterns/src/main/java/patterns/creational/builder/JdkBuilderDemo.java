package patterns.creational.builder;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Locale;
import java.util.stream.Stream;

// ANCHOR: jdk-builder-demo
public final class JdkBuilderDemo {
  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder().append("a").append(1).append('c');
    System.out.println(sb);

    Stream.Builder<String> sbuilder = Stream.builder();
    System.out.println(sbuilder.add("x").add("y").build().count());

    HttpRequest req =
        HttpRequest.newBuilder(URI.create("http://example.test/items"))
            .header("Accept", "text/plain")
            .GET()
            .build();
    System.out.println(req.method() + " " + req.uri() + " " + req.headers().firstValue("Accept").get());

    Locale locale = new Locale.Builder().setLanguage("fr").setRegion("CA").build();
    System.out.println(locale);
  }
}
// ANCHOR_END: jdk-builder-demo

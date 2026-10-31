package patterns.behavioral.chain;

import java.util.List;

public final class FilterChainDemo {
  // ANCHOR: filter-demo
  public static void main(String[] args) {
    Filter log =
        (r, c) -> {
          System.out.println("log in  " + r.path());
          String res = c.proceed(r);
          System.out.println("log out " + res);
          return res;
        };
    Filter auth = (r, c) -> r.user().isEmpty() ? "401" : c.proceed(r);
    Filter timing =
        (r, c) -> {
          System.out.println("timing start");
          return c.proceed(r);
        };
    List<Filter> filters = List.of(log, auth, timing);

    for (Request req : List.of(new Request("ann", "/home"), new Request("", "/admin"))) {
      String res = new FilterChain(filters, r -> "200 " + r.path()).proceed(req);
      System.out.println("=> " + res);
    }
  }
  // ANCHOR_END: filter-demo
}

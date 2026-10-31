package solutions.ch45;

import java.util.List;

/** Exercise 3: a filter chain with an index; an admin-only filter blocks other users. */
public final class SolutionAuthFilters {
  interface Filter {
    String handle(String user, Chain chain);
  }

  static final class Chain {
    private final List<Filter> filters;
    private int i;

    Chain(List<Filter> filters) {
      this.filters = filters;
    }

    String proceed(String user) {
      return i < filters.size() ? filters.get(i++).handle(user, this) : "ok " + user;
    }
  }

  public static void main(String[] args) {
    Filter named = (u, c) -> u.isEmpty() ? "anonymous" : c.proceed(u);
    Filter admin = (u, c) -> u.equals("root") ? c.proceed(u) : "forbidden " + u;
    for (String u : new String[] {"", "ann", "root"}) {
      System.out.println(new Chain(List.of(named, admin)).proceed(u));
    }
  }
}

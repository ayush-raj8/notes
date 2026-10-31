package patterns.structural.proxy.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import patterns.structural.proxy.Account;
import patterns.structural.proxy.LoggingProxies;
import patterns.structural.proxy.RealAccount;

public final class Ch36Solutions {
  // ANCHOR: ch36-ex1
  /** Ex1: caching proxy: repeated calls with the same key hit the cache. */
  interface Lookup {
    String find(String key);
  }

  static final class CachingLookup implements Lookup {
    private final Lookup target;
    private final Map<String, String> cache = new HashMap<>();

    CachingLookup(Lookup target) {
      this.target = target;
    }

    @Override
    public String find(String key) {
      return cache.computeIfAbsent(key, target::find);
    }
  }
  // ANCHOR_END: ch36-ex1

  // ANCHOR: ch36-ex2
  /** Ex2: counting proxy for any Lookup. */
  static final class CountingLookup implements Lookup {
    private final Lookup target;
    int calls;

    CountingLookup(Lookup target) {
      this.target = target;
    }

    @Override
    public String find(String key) {
      calls++;
      return target.find(key);
    }
  }
  // ANCHOR_END: ch36-ex2

  // ANCHOR: ch36-ex3
  /** Ex3: read-only proxy: only getters are forwarded. */
  static Account readOnly(Account target) {
    return new Account() {
      @Override
      public int balance() {
        return target.balance();
      }

      @Override
      public void withdraw(int amount) {
        throw new UnsupportedOperationException("read-only");
      }
    };
  }
  // ANCHOR_END: ch36-ex3

  // ANCHOR: ch36-ex4
  /** Ex4: dynamic proxy that records method names (reusing LoggingProxies). */
  static List<String> record() {
    List<String> log = new ArrayList<>();
    Account a = LoggingProxies.wrap(Account.class, new RealAccount(9), log);
    a.balance();
    a.withdraw(1);
    return log;
  }
  // ANCHOR_END: ch36-ex4

  // ANCHOR: ch36-ex5
  /** Ex5: lazy holder as a tiny virtual proxy using Supplier. */
  static final class Lazy<T> {
    private final java.util.function.Supplier<T> supplier;
    private T value;

    Lazy(java.util.function.Supplier<T> supplier) {
      this.supplier = supplier;
    }

    T get() {
      if (value == null) {
        value = supplier.get();
      }
      return value;
    }
  }
  // ANCHOR_END: ch36-ex5

  public static void main(String[] args) {
    CountingLookup slow = new CountingLookup(k -> k.toUpperCase());
    Lookup cached = new CachingLookup(slow);
    cached.find("a");
    cached.find("a");
    cached.find("b");
    System.out.println("ex1 target calls=" + slow.calls);
    System.out.println("ex2 " + slow.find("z") + " calls=" + slow.calls);
    try {
      readOnly(new RealAccount(5)).withdraw(1);
    } catch (UnsupportedOperationException e) {
      System.out.println("ex3 " + e.getMessage());
    }
    System.out.println("ex4 " + record());
    int[] built = {0};
    Lazy<String> lazy = new Lazy<>(() -> {
      built[0]++;
      return "v";
    });
    lazy.get();
    lazy.get();
    System.out.println("ex5 built=" + built[0]);
  }
}

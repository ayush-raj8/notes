package oop.compose;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: fragile-versions
/** Same subclass code, two versions of the base. Only the base's internal strategy changes. */
public final class FragileVersions {
  /** Base v1: {@code addAll} calls {@code add} (self-use). */
  static class BagV1 {
    protected final List<String> items = new ArrayList<>();

    public void add(String s) {
      items.add(s);
    }

    public void addAll(List<String> all) {
      for (String s : all) {
        add(s);
      }
    }
  }

  /** Base v2: {@code addAll} copies in bulk; {@code add} is no longer called. */
  static class BagV2 {
    protected final List<String> items = new ArrayList<>();

    public void add(String s) {
      items.add(s);
    }

    public void addAll(List<String> all) {
      items.addAll(all);
    }
  }

  static class CountingV1 extends BagV1 {
    int count;

    @Override
    public void add(String s) {
      count++;
      super.add(s);
    }

    @Override
    public void addAll(List<String> all) {
      count += all.size();
      super.addAll(all);
    }
  }

  static class CountingV2 extends BagV2 {
    int count;

    @Override
    public void add(String s) {
      count++;
      super.add(s);
    }

    @Override
    public void addAll(List<String> all) {
      count += all.size();
      super.addAll(all);
    }
  }

  public static void main(String[] args) {
    CountingV1 a = new CountingV1();
    a.addAll(List.of("x", "y", "z"));
    System.out.println("v1 count=" + a.count + " size=" + a.items.size());

    CountingV2 b = new CountingV2();
    b.addAll(List.of("x", "y", "z"));
    System.out.println("v2 count=" + b.count + " size=" + b.items.size());
  }
}
// ANCHOR_END: fragile-versions

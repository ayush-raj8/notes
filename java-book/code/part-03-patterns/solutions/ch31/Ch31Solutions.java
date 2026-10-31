package patterns.creational.builder.solutions;

import java.util.ArrayList;
import java.util.List;
import patterns.creational.builder.ReportBuilder;
import patterns.creational.builder.ReportDirector;

public final class Ch31Solutions {
  // ANCHOR: ch31-ex1
  /** Ex1: required name, optional age. */
  static final class Person {
    private final String name;
    private final int age;

    private Person(Builder b) {
      this.name = b.name;
      this.age = b.age;
    }

    static final class Builder {
      private final String name;
      private int age = -1;

      Builder(String name) {
        this.name = java.util.Objects.requireNonNull(name, "name");
      }

      Builder age(int age) {
        this.age = age;
        return this;
      }

      Person build() {
        return new Person(this);
      }
    }

    @Override
    public String toString() {
      return name + (age < 0 ? "" : " (" + age + ")");
    }
  }
  // ANCHOR_END: ch31-ex1

  // ANCHOR: ch31-ex2
  /** Ex2: validate a combination in build(), not in setters. */
  static final class Range {
    final int low;
    final int high;

    private Range(int low, int high) {
      this.low = low;
      this.high = high;
    }

    static final class Builder {
      private int low;
      private int high;

      Builder low(int v) {
        low = v;
        return this;
      }

      Builder high(int v) {
        high = v;
        return this;
      }

      Range build() {
        if (low > high) {
          throw new IllegalStateException("low > high");
        }
        return new Range(low, high);
      }
    }
  }
  // ANCHOR_END: ch31-ex2

  // ANCHOR: ch31-ex3
  /** Ex3: record with a nested builder; toBuilder() copies for modification. */
  record Settings(String host, int port) {
    static Builder builder() {
      return new Builder();
    }

    Builder toBuilder() {
      return new Builder().host(host).port(port);
    }

    static final class Builder {
      private String host = "localhost";
      private int port = 80;

      Builder host(String h) {
        host = h;
        return this;
      }

      Builder port(int p) {
        port = p;
        return this;
      }

      Settings build() {
        return new Settings(host, port);
      }
    }
  }
  // ANCHOR_END: ch31-ex3

  // ANCHOR: ch31-ex4
  /** Ex4: a defensive copy keeps a built product stable. */
  static final class Tags {
    final List<String> values;

    private Tags(List<String> values) {
      this.values = List.copyOf(values);
    }

    static final class Builder {
      private final List<String> values = new ArrayList<>();

      Builder add(String v) {
        values.add(v);
        return this;
      }

      Tags build() {
        return new Tags(values);
      }
    }
  }
  // ANCHOR_END: ch31-ex4

  // ANCHOR: ch31-ex5
  /** Ex5: a CSV builder reused by the existing director. */
  static final class CsvReportBuilder implements ReportBuilder {
    private final List<String> cells = new ArrayList<>();

    @Override
    public ReportBuilder title(String text) {
      cells.add(text);
      return this;
    }

    @Override
    public ReportBuilder line(String text) {
      cells.add(text);
      return this;
    }

    @Override
    public String build() {
      return String.join(",", cells);
    }
  }
  // ANCHOR_END: ch31-ex5

  public static void main(String[] args) {
    System.out.println("ex1 " + new Person.Builder("Ada").age(36).build() + " / "
        + new Person.Builder("Bob").build());
    try {
      new Range.Builder().low(5).high(1).build();
    } catch (IllegalStateException e) {
      System.out.println("ex2 " + e.getMessage());
    }
    Settings base = Settings.builder().host("a").build();
    System.out.println("ex3 " + base + " " + base.toBuilder().port(8080).build());
    Tags.Builder tb = new Tags.Builder().add("x");
    Tags t = tb.build();
    tb.add("y");
    System.out.println("ex4 " + t.values);
    System.out.println("ex5 " + new ReportDirector().sales(new CsvReportBuilder()));
  }
}

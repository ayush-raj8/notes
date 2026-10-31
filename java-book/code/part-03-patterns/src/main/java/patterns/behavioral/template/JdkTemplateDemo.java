package patterns.behavioral.template;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;

/** JDK templates: {@code AbstractList} and {@code InputStream} supply the skeleton. */
public final class JdkTemplateDemo {
  // ANCHOR: jdk-range
  /** Only {@code get} and {@code size} are written; the rest of {@code List} is inherited. */
  static final class RangeList extends AbstractList<Integer> {
    private final int start;
    private final int size;

    RangeList(int start, int size) {
      this.start = start;
      this.size = size;
    }

    @Override
    public Integer get(int index) {
      if (index < 0 || index >= size) throw new IndexOutOfBoundsException("index " + index);
      return start + index;
    }

    @Override
    public int size() {
      return size;
    }
  }
  // ANCHOR_END: jdk-range

  // ANCHOR: jdk-stream
  /** Only the single-byte {@code read()} is written; {@code readAllBytes} is inherited. */
  static final class Countdown extends InputStream {
    private int n = 3;

    @Override
    public int read() {
      return n > 0 ? 'a' + (--n) : -1;
    }
  }
  // ANCHOR_END: jdk-stream

  // ANCHOR: jdk-demo
  public static void main(String[] args) throws IOException {
    RangeList r = new RangeList(10, 5);
    System.out.println(r);
    System.out.println(r.contains(12) + " " + r.indexOf(13) + " " + r.subList(1, 3));
    System.out.println(r.equals(java.util.List.of(10, 11, 12, 13, 14)));
    System.out.println(new String(new Countdown().readAllBytes()));
  }
  // ANCHOR_END: jdk-demo
}

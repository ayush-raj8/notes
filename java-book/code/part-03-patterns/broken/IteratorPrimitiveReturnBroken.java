import java.util.Iterator;

public final class IteratorPrimitiveReturnBroken implements Iterator<Integer> {
  @Override
  public boolean hasNext() {
    return false;
  }

  @Override
  public int next() {
    return 0;
  }

  public static void main(String[] args) {}
}

import java.util.Iterator;

public final class IteratorMissingNextBroken implements Iterator<Integer> {
  @Override
  public boolean hasNext() {
    return false;
  }

  public static void main(String[] args) {
    System.out.println(new IteratorMissingNextBroken().hasNext());
  }
}

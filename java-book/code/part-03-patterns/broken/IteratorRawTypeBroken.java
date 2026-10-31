import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class IteratorRawTypeBroken {
  @SuppressWarnings({"rawtypes", "unchecked"})
  public static void main(String[] args) {
    List<String> list = new ArrayList<>(List.of("a"));
    Iterator it = list.iterator();
    String s = it.next();
    System.out.println(s);
  }
}

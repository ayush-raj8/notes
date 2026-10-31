import java.util.List;

public final class IteratorForEachOverIteratorBroken {
  public static void main(String[] args) {
    List<String> list = List.of("a", "b");
    for (String s : list.iterator()) {
      System.out.println(s);
    }
  }
}

package patterns.behavioral.memento;

import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** JDK mementos: {@code Matcher.toMatchResult()} and the shallow {@code ArrayList.clone()}. */
public final class JdkMementoDemo {
  // ANCHOR: jdk-demo
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    Matcher m = Pattern.compile("\\d+").matcher("a1 b22 c333");
    m.find();
    MatchResult first = m.toMatchResult();
    m.find();
    m.find();
    System.out.println("snapshot " + first.group() + " at " + first.start());
    System.out.println("matcher  " + m.group() + " at " + m.start());

    ArrayList<StringBuilder> original = new ArrayList<>();
    original.add(new StringBuilder("x"));
    ArrayList<StringBuilder> copy = (ArrayList<StringBuilder>) original.clone();
    original.add(new StringBuilder("new"));
    original.get(0).append("!");
    System.out.println("original " + original);
    System.out.println("clone    " + copy);
  }
  // ANCHOR_END: jdk-demo
}

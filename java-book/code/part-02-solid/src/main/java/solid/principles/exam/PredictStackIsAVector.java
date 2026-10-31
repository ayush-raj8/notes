package solid.principles.exam;

// ANCHOR: predict-stack
import java.util.Stack;

public final class PredictStackIsAVector {
  public static void main(String[] args) {
    Stack<String> s = new Stack<>();
    s.push("a");
    s.push("b");
    s.add(0, "c");
    System.out.println(s);
    System.out.println(s.peek());
    System.out.println(s.pop() + s.pop() + s.pop());
    System.out.println(s.empty());
  }
}
// ANCHOR_END: predict-stack

package oop.compose.exam;

import java.util.Stack;

// ANCHOR: predict-stack-bottom
public final class PredictStackBottom {
  public static void main(String[] args) {
    Stack<String> s = new Stack<>();
    s.push("a");
    s.push("b");
    s.push("c");
    s.remove(0);
    System.out.println(s.pop() + s.pop());
  }
}
// ANCHOR_END: predict-stack-bottom

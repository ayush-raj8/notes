package oop.coll.solutions;

import java.util.ArrayDeque;
import java.util.Deque;

/** Exercise 5: bracket matching with a Deque used as a stack. */
public final class SolutionBalancedBrackets {
  static boolean balanced(String s) {
    Deque<Character> stack = new ArrayDeque<>();
    for (char c : s.toCharArray()) {
      switch (c) {
        case '(' -> stack.push(')');
        case '[' -> stack.push(']');
        case '{' -> stack.push('}');
        case ')', ']', '}' -> {
          if (stack.isEmpty() || stack.pop() != c) {
            return false;
          }
        }
        default -> { }
      }
    }
    return stack.isEmpty();
  }

  public static void main(String[] args) {
    System.out.println(balanced("{[()]}") + " " + balanced("([)]") + " " + balanced("((") + " " + balanced(""));
  }
}

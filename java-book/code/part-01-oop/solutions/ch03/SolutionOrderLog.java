package oop.init.solutions;

import java.util.ArrayList;
import java.util.List;

class LogParent {
  static final List<String> LOG = new ArrayList<>();

  static {
    LOG.add("P-static");
  }

  {
    LOG.add("P-block");
  }

  LogParent() {
    LOG.add("P-ctor");
  }
}

class LogChild extends LogParent {
  static {
    LOG.add("C-static");
  }

  {
    LOG.add("C-block");
  }

  LogChild() {
    LOG.add("C-ctor");
  }
}

/** Exercise 4: record initialization steps in a list and print it. */
public final class SolutionOrderLog {
  public static void main(String[] args) {
    new LogChild();
    System.out.println(LogParent.LOG);
  }
}

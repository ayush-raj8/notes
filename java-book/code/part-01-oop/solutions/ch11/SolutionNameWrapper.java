package oop.compose.solutions;

/** Exercise 5: String is final, so wrap it and forward the methods you need. */
public final class SolutionNameWrapper {
  static final class Name {
    private final String value;

    Name(String value) {
      this.value = value;
    }

    int length() {
      return value.length();
    }

    Name upper() {
      return new Name(value.toUpperCase());
    }

    @Override
    public String toString() {
      return value;
    }
  }

  public static void main(String[] args) {
    Name n = new Name("ada").upper();
    System.out.println(n + " " + n.length());
  }
}

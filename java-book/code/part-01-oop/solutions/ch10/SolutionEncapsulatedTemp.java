package oop.abstracts.solutions;

/** Exercise 5: encapsulate a field with a validated setter and a derived getter. */
public final class SolutionEncapsulatedTemp {
  static final class Temperature {
    private double celsius;

    Temperature(double celsius) {
      setCelsius(celsius);
    }

    void setCelsius(double c) {
      if (c < -273.15) {
        throw new IllegalArgumentException("below absolute zero");
      }
      this.celsius = c;
    }

    double fahrenheit() { // derived, no stored duplicate
      return celsius * 9 / 5 + 32;
    }
  }

  public static void main(String[] args) {
    Temperature t = new Temperature(100);
    System.out.println(t.fahrenheit());
    try {
      t.setCelsius(-300);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
    System.out.println(t.fahrenheit());
  }
}

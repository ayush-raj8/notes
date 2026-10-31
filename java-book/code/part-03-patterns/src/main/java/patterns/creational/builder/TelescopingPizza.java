package patterns.creational.builder;

// ANCHOR: telescoping-pizza
/** Anti-pattern: constructor overloads grow with every option. */
public final class TelescopingPizza {
  private final String size;
  private final boolean cheese;
  private final boolean pepperoni;
  private final boolean olives;

  public TelescopingPizza(String size) {
    this(size, false);
  }

  public TelescopingPizza(String size, boolean cheese) {
    this(size, cheese, false);
  }

  public TelescopingPizza(String size, boolean cheese, boolean pepperoni) {
    this(size, cheese, pepperoni, false);
  }

  public TelescopingPizza(String size, boolean cheese, boolean pepperoni, boolean olives) {
    this.size = size;
    this.cheese = cheese;
    this.pepperoni = pepperoni;
    this.olives = olives;
  }

  @Override
  public String toString() {
    return size + " cheese=" + cheese + " pepperoni=" + pepperoni + " olives=" + olives;
  }
}
// ANCHOR_END: telescoping-pizza

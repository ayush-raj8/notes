package patterns.creational.builder;

// ANCHOR: pizza
/** Immutable product with a static nested Builder (the Effective Java form). */
public final class Pizza {
  private final String size;
  private final boolean cheese;
  private final boolean pepperoni;
  private final boolean olives;

  private Pizza(Builder b) {
    this.size = b.size;
    this.cheese = b.cheese;
    this.pepperoni = b.pepperoni;
    this.olives = b.olives;
  }

  public static Builder builder(String size) {
    return new Builder(size);
  }

  public static final class Builder {
    private final String size; // required
    private boolean cheese; // optional, default false
    private boolean pepperoni;
    private boolean olives;

    private Builder(String size) {
      this.size = size;
    }

    public Builder cheese() {
      this.cheese = true;
      return this;
    }

    public Builder pepperoni() {
      this.pepperoni = true;
      return this;
    }

    public Builder olives() {
      this.olives = true;
      return this;
    }

    public Pizza build() {
      if (size == null || size.isBlank()) {
        throw new IllegalStateException("size is required");
      }
      if (cheese && pepperoni && olives) {
        throw new IllegalStateException("at most two toppings");
      }
      return new Pizza(this);
    }
  }

  public String size() {
    return size;
  }

  @Override
  public String toString() {
    return "Pizza[size=" + size + ", cheese=" + cheese + ", pepperoni=" + pepperoni
        + ", olives=" + olives + "]";
  }
}
// ANCHOR_END: pizza

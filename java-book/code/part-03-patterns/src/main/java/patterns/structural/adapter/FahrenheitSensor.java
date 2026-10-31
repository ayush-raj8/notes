package patterns.structural.adapter;

// ANCHOR: fahrenheit-sensor
/** Adaptee that reports Fahrenheit. */
public final class FahrenheitSensor {
  private final double reading;

  public FahrenheitSensor(double reading) {
    this.reading = reading;
  }

  public double fahrenheit() {
    return reading;
  }

  /** Lambda adapter from the adaptee to the target. */
  public static CelsiusSource adapt(FahrenheitSensor s) {
    return () -> (s.fahrenheit() - 32) * 5 / 9;
  }
}
// ANCHOR_END: fahrenheit-sensor

package patterns.structural.adapter;

// ANCHOR: adapter-demo
public final class AdapterDemo {
  static void report(Printer p) {
    System.out.println(p.print("invoice"));
  }

  public static void main(String[] args) {
    report(new PrinterObjectAdapter(new LegacyPrinter()));
    report(new PrinterClassAdapter());
    report(text -> "LAMBDA[" + text + "]");

    CelsiusSource boiling = FahrenheitSensor.adapt(new FahrenheitSensor(212));
    System.out.println(boiling.celsius());
  }
}
// ANCHOR_END: adapter-demo

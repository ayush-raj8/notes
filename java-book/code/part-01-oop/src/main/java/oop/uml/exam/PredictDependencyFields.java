package oop.uml.exam;

import java.util.Arrays;

// ANCHOR: predict-dependency-fields
public final class PredictDependencyFields {
  static final class Printer {}

  static final class Report { // ..> Printer
    String print(Printer p) {
      return "printed";
    }
  }

  static final class Office { // --> Printer
    private final Printer printer = new Printer();
  }

  static long fieldsOfType(Class<?> owner, Class<?> type) {
    return Arrays.stream(owner.getDeclaredFields()).filter(f -> f.getType() == type).count();
  }

  public static void main(String[] args) {
    System.out.println(fieldsOfType(Report.class, Printer.class));
    System.out.println(fieldsOfType(Office.class, Printer.class));
  }
}
// ANCHOR_END: predict-dependency-fields

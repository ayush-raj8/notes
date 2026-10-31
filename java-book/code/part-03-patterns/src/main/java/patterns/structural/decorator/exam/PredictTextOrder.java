package patterns.structural.decorator.exam;

import patterns.structural.decorator.TextSource;

// ANCHOR: predict-text-order
public final class PredictTextOrder {
  public static void main(String[] args) {
    TextSource raw = TextSource.of(" ab ");
    System.out.println("[" + raw.then(String::trim).then(s -> s + "!").read() + "]");
    System.out.println("[" + raw.then(s -> s + "!").then(String::trim).read() + "]");
    System.out.println("[" + raw.read() + "]");
  }
}
// ANCHOR_END: predict-text-order

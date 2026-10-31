package patterns.structural.decorator;

import java.util.function.UnaryOperator;

// ANCHOR: text-source
/** Second example: decorators are built from a function, and ORDER changes the result. */
public interface TextSource {
  String read();

  static TextSource of(String text) {
    return () -> text;
  }

  /** Decorating step: returns a new source that post-processes this one. */
  default TextSource then(UnaryOperator<String> step) {
    return () -> step.apply(read());
  }
}
// ANCHOR_END: text-source

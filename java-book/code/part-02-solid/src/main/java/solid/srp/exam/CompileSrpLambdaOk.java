package solid.srp.exam;

// ANCHOR: compile-ok-lambda
import solid.srp.parts.ReceiptNotifier;

public final class CompileSrpLambdaOk {
  public static void main(String[] args) {
    ReceiptNotifier console = (id, cents) -> System.out.println("sent " + id + " " + cents);
    console.send("OK-1", 500);
  }
}
// ANCHOR_END: compile-ok-lambda

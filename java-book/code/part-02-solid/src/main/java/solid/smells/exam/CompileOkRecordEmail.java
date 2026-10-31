package solid.smells.exam;

// ANCHOR: compile-ok-record-email
import solid.smells.primitive.PrimitiveObsessionDemo.Email;
import solid.smells.primitive.PrimitiveObsessionDemo.Money;

public final class CompileOkRecordEmail {
  public static void main(String[] args) {
    Email e = new Email("  Asha@X.org ");
    System.out.println(e.value() + " " + e.domain());
    System.out.println(new Email("asha@x.org").equals(e));
    System.out.println(new Money(1999) + " " + new Money(1999).cents());
  }
}
// ANCHOR_END: compile-ok-record-email

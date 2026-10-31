package oop.uml.exam;

// ANCHOR: predict-static-member
/** Underlined member in UML = static: one copy for the class. */
public final class PredictStaticMember {
  static final class Ticket {
    static int issued; // underlined
    final int number; // instance attribute

    Ticket() {
      number = ++issued;
    }
  }

  public static void main(String[] args) {
    Ticket a = new Ticket();
    Ticket b = new Ticket();
    Ticket c = new Ticket();
    System.out.println(a.number + " " + b.number + " " + c.number);
    System.out.println(Ticket.issued + " " + a.issued);
  }
}
// ANCHOR_END: predict-static-member

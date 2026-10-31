package solid.dip;

import solid.dip.after.domain.Mailer;

/** Mailer.send takes two parameters; the lambda takes one. */
public final class DipLambdaArityBroken {
  public static void main(String[] args) {
    Mailer m = body -> System.out.println(body);
  }
}

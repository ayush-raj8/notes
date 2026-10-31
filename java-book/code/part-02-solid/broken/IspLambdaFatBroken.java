package solid.isp;

import solid.isp.before.Machine;

/** A fat interface cannot be implemented with a lambda; a role interface can. */
public final class IspLambdaFatBroken {
  public static void main(String[] args) {
    Machine m = () -> "page";
  }
}

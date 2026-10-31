package solid.smells.exam;

// ANCHOR: predict-switch-default
import solid.smells.switchtype.SwitchTypeDemo.After;
import solid.smells.switchtype.SwitchTypeDemo.Before;
import solid.smells.switchtype.SwitchTypeDemo.Cash;
import solid.smells.switchtype.SwitchTypeDemo.Kind;
import solid.smells.switchtype.SwitchTypeDemo.Wallet;

public final class PredictSwitchDefault {
  public static void main(String[] args) {
    for (Kind k : Kind.values()) {
      System.out.println(k + " " + Before.fee(k, 2000));
    }
    System.out.println(After.fee(new Wallet(2000)) + " " + After.fee(new Cash(2000)));
  }
}
// ANCHOR_END: predict-switch-default

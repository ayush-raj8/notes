package lld.atm.exam;

import lld.atm.AtmState;
import lld.atm.Operation;

// ANCHOR: predict-allows
public class PredictAllows {
  public static void main(String[] args) {
    for (AtmState s : AtmState.values()) {
      StringBuilder sb = new StringBuilder(s.name()).append(':');
      for (Operation op : Operation.values()) {
        if (s.allows(op)) {
          sb.append(' ').append(op);
        }
      }
      System.out.println(sb);
    }
  }
}
// ANCHOR_END: predict-allows

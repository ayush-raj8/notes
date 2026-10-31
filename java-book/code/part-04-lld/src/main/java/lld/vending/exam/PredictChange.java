package lld.vending.exam;

import lld.vending.Coin;
import lld.vending.CoinBank;

// ANCHOR: predict-change
public class PredictChange {
  public static void main(String[] args) {
    CoinBank bank = new CoinBank();
    bank.deposit(Coin.QUARTER);
    for (int i = 0; i < 3; i++) {
      bank.deposit(Coin.DIME);
    }
    System.out.println(bank.withdraw(30));
    System.out.println(bank.withdraw(5));
    System.out.println(bank.withdraw(25));
    System.out.println(bank.count(Coin.QUARTER) + " " + bank.count(Coin.DIME));
  }
}
// ANCHOR_END: predict-change

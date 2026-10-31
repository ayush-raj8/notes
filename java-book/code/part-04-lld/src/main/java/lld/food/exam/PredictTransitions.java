package lld.food.exam;

import lld.food.OrderStatus;

public class PredictTransitions {
  public static void main(String[] args) {
    System.out.println(OrderStatus.PLACED.next());
    System.out.println(OrderStatus.DELIVERED.next());
    System.out.println(OrderStatus.PLACED.canGoTo(OrderStatus.READY));
    System.out.println(OrderStatus.PREPARING.canGoTo(OrderStatus.CANCELLED));
    System.out.println(OrderStatus.CANCELLED.isTerminal());
    System.out.println(OrderStatus.valueOf("READY").ordinal());
  }
}

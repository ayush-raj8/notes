package oop.coll;

import java.util.List;

public final class CollLambdaCaptureBroken {
  public static void main(String[] args) {
    int sum = 0;
    List.of(1, 2, 3).forEach(n -> sum += n);
    System.out.println(sum);
  }
}

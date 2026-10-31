package patterns.creational.factorymethod.exam;

import patterns.creational.factorymethod.Notifier;
import patterns.creational.factorymethod.NotifierCreator;

// ANCHOR: predict-anonymous-creator
public final class PredictAnonymousCreator {
  public static void main(String[] args) {
    NotifierCreator c =
        new NotifierCreator() {
          @Override
          protected Notifier create() {
            return msg -> "LAMBDA:" + msg.toUpperCase();
          }
        };
    System.out.println(c.deliver("ok"));
  }
}
// ANCHOR_END: predict-anonymous-creator

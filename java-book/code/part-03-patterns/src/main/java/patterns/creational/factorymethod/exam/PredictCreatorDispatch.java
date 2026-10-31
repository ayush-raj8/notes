package patterns.creational.factorymethod.exam;

import patterns.creational.factorymethod.EmailCreator;
import patterns.creational.factorymethod.NotifierCreator;
import patterns.creational.factorymethod.SmsCreator;

// ANCHOR: predict-creator-dispatch
public final class PredictCreatorDispatch {
  public static void main(String[] args) {
    NotifierCreator c = new SmsCreator();
    System.out.println(c.deliver("x"));
    c = new EmailCreator();
    System.out.println(c.deliver("y"));
  }
}
// ANCHOR_END: predict-creator-dispatch

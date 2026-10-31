package lld.notification.exam;

import java.util.EnumSet;
import lld.notification.Channel;
import lld.notification.Priority;

public class PredictEnumSet {
  public static void main(String[] args) {
    System.out.println(EnumSet.of(Channel.SMS, Channel.EMAIL));
    System.out.println(EnumSet.complementOf(EnumSet.of(Channel.PUSH)));
    System.out.println(Priority.HIGH.compareTo(Priority.LOW));
    System.out.println(Priority.valueOf("NORMAL").ordinal());
    try {
      Priority.valueOf("normal");
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
    }
  }
}

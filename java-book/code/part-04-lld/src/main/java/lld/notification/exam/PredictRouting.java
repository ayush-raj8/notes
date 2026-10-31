package lld.notification.exam;

import java.util.EnumSet;
import lld.notification.*;

public class PredictRouting {
  public static void main(String[] args) {
    RoutingStrategy r = new DefaultRouting();
    System.out.println(r.plan(Priority.HIGH, EnumSet.allOf(Channel.class)));
    System.out.println(r.plan(Priority.NORMAL, EnumSet.of(Channel.SMS, Channel.EMAIL)));
    System.out.println(r.plan(Priority.LOW, EnumSet.of(Channel.SMS)));
    System.out.println(r.plan(Priority.LOW, EnumSet.noneOf(Channel.class)).channels().isEmpty());
  }
}

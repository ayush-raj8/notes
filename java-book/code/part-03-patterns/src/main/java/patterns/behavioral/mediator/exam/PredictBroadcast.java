package patterns.behavioral.mediator.exam;

import patterns.behavioral.mediator.ChatRoom;
import patterns.behavioral.mediator.User;

// ANCHOR: predict-broadcast
public final class PredictBroadcast {
  public static void main(String[] args) {
    ChatRoom room = new ChatRoom();
    User a = room.join("a");
    User b = room.join("b");
    User c = room.join("c");
    a.say("1");
    b.say("2");
    System.out.println(a.inbox());
    System.out.println(b.inbox());
    System.out.println(c.inbox());
  }
}
// ANCHOR_END: predict-broadcast

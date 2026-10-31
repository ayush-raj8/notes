package patterns.behavioral.mediator.exam;

import patterns.behavioral.mediator.ChatRoom;
import patterns.behavioral.mediator.User;

// ANCHOR: predict-rejoin
public final class PredictRejoin {
  public static void main(String[] args) {
    ChatRoom room = new ChatRoom();
    User first = room.join("sam");
    User second = room.join("sam");
    User other = room.join("zoe");
    System.out.println(other.whisper("sam", "hi"));
    System.out.println(first.inbox().size() + " " + second.inbox().size());
    first.say("anyone?");
    System.out.println(second.inbox());
    System.out.println(other.inbox());
  }
}
// ANCHOR_END: predict-rejoin

package patterns.behavioral.mediator;

public final class ChatDemo {
  // ANCHOR: chat-demo
  public static void main(String[] args) {
    ChatRoom room = new ChatRoom();
    User ann = room.join("ann");
    User bob = room.join("bob");
    User cy = room.join("cy");

    ann.say("hello all");
    System.out.println(bob.whisper("cy", "psst"));
    System.out.println(bob.whisper("zed", "anyone?"));

    System.out.println("ann: " + ann.inbox());
    System.out.println("bob: " + bob.inbox());
    System.out.println("cy:  " + cy.inbox());
  }
  // ANCHOR_END: chat-demo
}

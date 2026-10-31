package patterns.behavioral.mediator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TranscriptDemo {
  // ANCHOR: transcript-demo
  public static void main(String[] args) throws IOException {
    Path log = Files.createTempFile("room", ".log");
    try {
      TranscriptRoom room = new TranscriptRoom(log);
      User ann = room.join("ann");
      User bob = room.join("bob");
      room.join("cy");
      ann.say("hi");
      bob.whisper("ann", "ok");
      System.out.print(Files.readString(log));
      System.out.println("ann inbox: " + ann.inbox());
    } finally {
      Files.deleteIfExists(log);
    }
  }
  // ANCHOR_END: transcript-demo
}

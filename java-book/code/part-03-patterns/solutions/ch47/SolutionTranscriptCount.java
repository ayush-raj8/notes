package solutions.ch47;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import patterns.behavioral.mediator.TranscriptRoom;
import patterns.behavioral.mediator.User;

/** Exercise 4: count deliveries per recipient by reading the mediator's real transcript file. */
public final class SolutionTranscriptCount {
  public static void main(String[] args) throws IOException {
    Path log = Files.createTempFile("count", ".log");
    try {
      TranscriptRoom room = new TranscriptRoom(log);
      User a = room.join("a");
      User b = room.join("b");
      room.join("c");
      a.say("x");
      b.say("y");
      long toC = Files.readAllLines(log).stream().filter(l -> l.contains("->c:")).count();
      System.out.println("lines=" + Files.readAllLines(log).size() + " toC=" + toC);
    } finally {
      Files.deleteIfExists(log);
    }
  }
}

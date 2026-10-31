package patterns.behavioral.mediator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// ANCHOR: transcript-room
/** Real I/O: a new routing rule (log every delivery) is added in the mediator only. */
public final class TranscriptRoom extends ChatRoom {
  private final Path transcript;

  public TranscriptRoom(Path transcript) {
    this.transcript = transcript;
  }

  @Override
  protected void deliver(User from, User to, String message) {
    try {
      Files.writeString(
          transcript,
          from.name() + "->" + to.name() + ": " + message + "\n",
          StandardCharsets.UTF_8,
          StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    super.deliver(from, to, message);
  }
}
// ANCHOR_END: transcript-room

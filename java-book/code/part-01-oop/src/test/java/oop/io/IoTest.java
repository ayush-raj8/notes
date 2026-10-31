package oop.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Scanner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class IoTest {
  @TempDir Path tmp;

  @Test
  void pathOperations() {
    assertEquals(Path.of("a/c"), Path.of("a", "b", "..", "c").normalize());
    assertEquals(Path.of("/abs/x"), Path.of("data").resolve("/abs/x"));
    assertEquals(Path.of("../c/d"), Path.of("a/b").relativize(Path.of("a/c/d")));
  }

  @Test
  void readReturnsUnsignedByteAndMinusOneAtEof() throws IOException {
    var in = new ByteArrayInputStream(new byte[] {(byte) 0xFF});
    assertEquals(255, in.read());
    assertEquals(-1, in.read());
  }

  @Test
  void readCountMattersWhenCopying() throws IOException {
    byte[] data = "abcdefghij".getBytes(StandardCharsets.US_ASCII);
    assertEquals("abcdefghij", ReadCountGotcha.copyCorrect(new ByteArrayInputStream(data)));
    assertEquals("abcdefghijgh", ReadCountGotcha.copyBuggy(new ByteArrayInputStream(data)));
  }

  @Test
  void charsetLengthsDiffer() {
    String s = "caf\u00e9";
    assertEquals(5, s.getBytes(StandardCharsets.UTF_8).length);
    assertEquals(4, s.getBytes(StandardCharsets.ISO_8859_1).length);
    assertArrayEquals(new byte[] {-61, -87}, "\u00e9".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  void readingLatin1AsUtf8FailsLoudly() throws IOException {
    Path f = tmp.resolve("l1.txt");
    Files.write(f, "caf\u00e9".getBytes(StandardCharsets.ISO_8859_1));
    assertThrows(MalformedInputException.class, () -> Files.readString(f));
  }

  @Test
  void bufferedWriterNeedsFlush() throws IOException {
    Path f = tmp.resolve("o.txt");
    BufferedWriter w = new BufferedWriter(new FileWriter(f.toFile()));
    w.write("data");
    assertEquals(0, Files.size(f));
    w.close();
    assertEquals(4, Files.size(f));
  }

  @Test
  void scannerNextIntLeavesNewline() {
    Scanner sc = new Scanner("7\nhello\n");
    assertEquals(7, sc.nextInt());
    assertEquals("", sc.nextLine());
    assertEquals("hello", sc.nextLine());
  }

  @Test
  void transientFieldIsNotRestored() throws Exception {
    var copy = SerialBasics.roundTrip(new SerialBasics.Account("a", 1, "pw"));
    assertNull(copy.password);
    assertEquals(1, copy.balance);
  }

  @Test
  void customSerializationRestoresSecret() throws Exception {
    var copy = SerialBasics.roundTrip(new SerialCustom.User("u", "s3"));
    assertEquals("s3", copy.secret());
  }

  @Test
  void nonSerializableFieldFails() {
    assertThrows(NotSerializableException.class, () -> SerialBasics.roundTrip(new SerialNotSerializableGotcha.Car()));
  }

  @Test
  void deleteSemantics() throws IOException {
    Path missing = tmp.resolve("nope");
    assertFalse(missing.toFile().delete());
    assertThrows(NoSuchFileException.class, () -> Files.delete(missing));
    assertFalse(Files.deleteIfExists(missing));
    Path f = tmp.resolve("x");
    Files.writeString(f, "1");
    assertTrue(Files.deleteIfExists(f));
  }
}

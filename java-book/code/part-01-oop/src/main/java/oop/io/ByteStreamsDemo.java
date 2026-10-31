package oop.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;

// ANCHOR: byte-streams
public final class ByteStreamsDemo {
  public static void main(String[] args) throws IOException {
    Path dir = IoTemp.newDir();
    try {
      Path file = dir.resolve("data.bin");
      try (FileOutputStream out = new FileOutputStream(file.toFile())) {
        out.write(65);
        out.write(255);
        out.write(new byte[] {1, 2, 3});
      }

      try (FileInputStream in = new FileInputStream(file.toFile())) {
        int b;
        StringBuilder sb = new StringBuilder();
        while ((b = in.read()) != -1) {
          sb.append(b).append(' ');
        }
        System.out.println(sb.toString().trim());
      }

      try (InputStream in = new BufferedInputStream(new FileInputStream(file.toFile()))) {
        byte[] all = in.readAllBytes();
        System.out.println(Arrays.toString(all));
      }

      ByteArrayOutputStream mem = new ByteArrayOutputStream();
      try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(mem))) {
        out.writeInt(258);
        out.writeUTF("hi");
        out.writeBoolean(true);
      }
      byte[] bytes = mem.toByteArray();
      System.out.println(bytes.length + " " + Arrays.toString(bytes));
      try (DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes))) {
        System.out.println(in.readInt() + " " + in.readUTF() + " " + in.readBoolean());
      }
    } finally {
      IoTemp.deleteTree(dir);
    }
  }
}
// ANCHOR_END: byte-streams

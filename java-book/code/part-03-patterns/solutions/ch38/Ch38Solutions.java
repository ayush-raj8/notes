package patterns.structural.bridge.solutions;

import java.util.ArrayList;
import java.util.List;

public final class Ch38Solutions {
  // ANCHOR: ch38-ex1
  /** Ex1: message abstraction x channel implementor (2 + 2 classes, 4 combinations). */
  interface Channel {
    String send(String text);
  }

  static final class Email implements Channel {
    @Override
    public String send(String text) {
      return "email:" + text;
    }
  }

  static final class Sms implements Channel {
    @Override
    public String send(String text) {
      return "sms:" + text;
    }
  }

  abstract static class Message {
    protected final Channel channel;

    Message(Channel channel) {
      this.channel = channel;
    }

    abstract String deliver();
  }

  static final class Alert extends Message {
    Alert(Channel c) {
      super(c);
    }

    @Override
    String deliver() {
      return channel.send("ALERT");
    }
  }

  static final class Digest extends Message {
    Digest(Channel c) {
      super(c);
    }

    @Override
    String deliver() {
      return channel.send("digest");
    }
  }
  // ANCHOR_END: ch38-ex1

  // ANCHOR: ch38-ex2
  /** Ex2: a logging abstraction that can switch its sink at run time. */
  interface Sink {
    void write(String line);
  }

  static final class Logger {
    private Sink sink;

    Logger(Sink sink) {
      this.sink = sink;
    }

    void switchTo(Sink next) {
      this.sink = next;
    }

    void info(String msg) {
      sink.write("INFO " + msg);
    }
  }
  // ANCHOR_END: ch38-ex2

  // ANCHOR: ch38-ex3
  /** Ex3: Sink has one abstract method, so a lambda can be the implementor. */
  static Sink prefixed(String prefix, List<String> into) {
    return line -> into.add(prefix + line);
  }
  // ANCHOR_END: ch38-ex3

  // ANCHOR: ch38-ex4
  /** Ex4: refined abstraction: a Message that retries the channel result in uppercase. */
  static final class Shout extends Message {
    Shout(Channel c) {
      super(c);
    }

    @Override
    String deliver() {
      return channel.send("hello").toUpperCase();
    }
  }
  // ANCHOR_END: ch38-ex4

  // ANCHOR: ch38-ex5
  /** Ex5: how many classes without a bridge? 3 abstractions x 4 implementors. */
  static String classCount(int abstractions, int implementors) {
    return (abstractions * implementors) + " without bridge, " + (abstractions + implementors) + " with bridge";
  }
  // ANCHOR_END: ch38-ex5

  public static void main(String[] args) {
    System.out.println("ex1 " + new Alert(new Sms()).deliver() + " " + new Digest(new Email()).deliver());
    List<String> lines = new ArrayList<>();
    Logger log = new Logger(lines::add);
    log.info("a");
    List<String> other = new ArrayList<>();
    log.switchTo(other::add);
    log.info("b");
    System.out.println("ex2 " + lines + " " + other);
    List<String> tagged = new ArrayList<>();
    new Logger(prefixed("[app] ", tagged)).info("up");
    System.out.println("ex3 " + tagged);
    System.out.println("ex4 " + new Shout(new Email()).deliver());
    System.out.println("ex5 " + classCount(3, 4));
  }
}

import lld.notification.Channel;

public class EnumSwitchBroken {
  static String label(Channel c) {
    return switch (c) {
      case EMAIL -> "mail";
      case SMS -> "text";
    };
  }
}

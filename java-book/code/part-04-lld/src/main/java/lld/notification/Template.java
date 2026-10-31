package lld.notification;

import java.util.Map;

/** "Hello {name}" style template. Literal replace (not regex) so values like "$5" are safe. */
public record Template(String subject, String body) {
  public Message render(Map<String, String> params) {
    return new Message(fill(subject, params), fill(body, params));
  }

  private static String fill(String text, Map<String, String> params) {
    String out = text;
    for (Map.Entry<String, String> e : params.entrySet()) {
      out = out.replace("{" + e.getKey() + "}", e.getValue());
    }
    int open = out.indexOf('{');
    int close = open < 0 ? -1 : out.indexOf('}', open);
    if (close > open) {
      throw new IllegalArgumentException("missing param: " + out.substring(open + 1, close));
    }
    return out;
  }
}

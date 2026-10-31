package solid.isp;

import solid.isp.after.Printer;

/** Interface methods are public; the implementation may not be less visible. */
public final class IspWeakerAccessBroken implements Printer {
  @Override
  String print(String doc) {
    return doc;
  }
}

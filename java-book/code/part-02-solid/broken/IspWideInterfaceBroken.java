package solid.isp;

import solid.isp.before.Machine;

/** Adding fax() to the fat interface broke every implementer that never faxed. */
public final class IspWideInterfaceBroken implements Machine {
  @Override
  public String print(String doc) {
    return doc;
  }

  @Override
  public String scan() {
    return "page";
  }
}

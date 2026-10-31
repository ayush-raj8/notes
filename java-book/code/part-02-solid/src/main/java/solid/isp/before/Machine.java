package solid.isp.before;

// ANCHOR: machine
public interface Machine {
  String print(String doc);

  String scan();

  boolean fax(String doc);
}
// ANCHOR_END: machine

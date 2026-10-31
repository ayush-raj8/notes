package oop.access;

// ANCHOR: access-levels
/** One member per access level. Every member is visible inside this class. */
public class AccessLevels {
  public String pub = "public";
  protected String prot = "protected";
  String pkg = "package-private";
  private String priv = "private";

  public String inside() {
    return pub + "," + prot + "," + pkg + "," + priv;
  }

  public static void main(String[] args) {
    System.out.println(new AccessLevels().inside());
  }
}
// ANCHOR_END: access-levels

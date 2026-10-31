package oop.abstracts;

interface IsUtil {
  static String tag() {
    return "tag";
  }
}

class IsUtilImpl implements IsUtil {}

/** Not allowed: static interface methods are not inherited by implementing classes. */
public class AbsInterfaceStaticInheritedBroken {
  public static void main(String[] args) {
    System.out.println(IsUtilImpl.tag());
  }
}

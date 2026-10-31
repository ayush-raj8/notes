package oop.poly;

// ANCHOR: poly-array-store
/** Arrays are covariant: the compiler allows it, the JVM checks every store. */
public final class PolyArrayStore {
  public static void main(String[] args) {
    Object[] objs = new String[2]; // legal: String[] is an Object[]
    objs[0] = "ok";
    try {
      objs[1] = Integer.valueOf(1); // compiles; the real array is String[]
    } catch (ArrayStoreException e) {
      System.out.println("ASE: " + e.getMessage());
    }
    System.out.println(objs.getClass().getSimpleName() + " " + objs[0] + " " + objs[1]);
  }
}
// ANCHOR_END: poly-array-store

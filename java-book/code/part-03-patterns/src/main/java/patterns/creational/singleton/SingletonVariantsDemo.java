package patterns.creational.singleton;

// ANCHOR: singleton-variants-demo
public final class SingletonVariantsDemo {
  public static void main(String[] args) {
    System.out.println("-- eager");
    System.out.println(EagerSingleton.getInstance() == EagerSingleton.getInstance());
    System.out.println("-- synchronized");
    System.out.println(SynchronizedSingleton.getInstance() == SynchronizedSingleton.getInstance());
    System.out.println("-- dcl");
    System.out.println(DclSingleton.getInstance() == DclSingleton.getInstance());
    System.out.println("-- holder");
    System.out.println(HolderSingleton.getInstance() == HolderSingleton.getInstance());
    System.out.println("-- enum");
    System.out.println(EnumSingleton.INSTANCE == EnumSingleton.valueOf("INSTANCE"));
  }
}
// ANCHOR_END: singleton-variants-demo

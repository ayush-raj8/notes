package oop.abstracts;

interface IfaConst {
  int MAX = 10;
}

/** Not allowed: interface fields are implicitly public static final. */
public class AbsInterfaceFieldAssignBroken {
  public static void main(String[] args) {
    IfaConst.MAX = 20;
    System.out.println(IfaConst.MAX);
  }
}

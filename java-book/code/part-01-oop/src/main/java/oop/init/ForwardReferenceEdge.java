package oop.init;

// ANCHOR: forward-reference-edge
class ForwardHolder {
  int viaMethod = readLater();
  int viaThis = this.later + 1;
  int later = 5;

  int readLater() {
    return later;
  }
}

public final class ForwardReferenceEdge {
  public static void main(String[] args) {
    ForwardHolder h = new ForwardHolder();
    System.out.println("viaMethod=" + h.viaMethod);
    System.out.println("viaThis=" + h.viaThis);
    System.out.println("later=" + h.later);
  }
}
// ANCHOR_END: forward-reference-edge

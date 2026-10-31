package oop.abstracts.exam;

// ANCHOR: predict-class-beats-default
interface PcbSpeaker {
  default String speak() {
    return "interface";
  }
}

class PcbParent {
  public String speak() {
    return "parent";
  }
}

class PcbChild extends PcbParent implements PcbSpeaker {}

class PcbOverride extends PcbParent implements PcbSpeaker {
  @Override
  public String speak() {
    return "override+" + PcbSpeaker.super.speak();
  }
}

public final class PredictClassBeatsDefault {
  public static void main(String[] args) {
    System.out.println(new PcbChild().speak());
    PcbSpeaker s = new PcbChild();
    System.out.println(s.speak());
    System.out.println(new PcbOverride().speak());
  }
}
// ANCHOR_END: predict-class-beats-default

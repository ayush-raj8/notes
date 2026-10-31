package patterns.structural.bridge;

// ANCHOR: remote-demo
public final class RemoteDemo {
  public static void main(String[] args) {
    Remote basic = new Remote(new Tv());
    basic.power();
    basic.volumeUp();
    System.out.println("tv    " + basic.status());

    AdvancedRemote adv = new AdvancedRemote(new Radio());
    adv.power();
    adv.volumeUp();
    adv.volumeUp();
    adv.volumeUp();
    System.out.println("radio " + adv.status());
    adv.mute();
    System.out.println("radio " + adv.status());
  }
}
// ANCHOR_END: remote-demo

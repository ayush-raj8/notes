package patterns.structural.bridge;

// ANCHOR: remote
public class Remote {
  protected final Device device;

  public Remote(Device device) {
    this.device = device;
  }

  public void power() {
    device.setOn(!device.isOn());
  }

  public void volumeUp() {
    device.setVolume(device.volume() + 10);
  }

  public void volumeDown() {
    device.setVolume(device.volume() - 10);
  }

  public String status() {
    return (device.isOn() ? "on" : "off") + " vol=" + device.volume();
  }
}
// ANCHOR_END: remote

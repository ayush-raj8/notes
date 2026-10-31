package patterns.structural.bridge;

// ANCHOR: advanced-remote
/** Refined abstraction: adds mute without touching any Device class. */
public class AdvancedRemote extends Remote {
  public AdvancedRemote(Device device) {
    super(device);
  }

  public void mute() {
    device.setVolume(0);
  }
}
// ANCHOR_END: advanced-remote

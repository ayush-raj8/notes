package patterns.structural.bridge;

// ANCHOR: device
/** Implementor for the remote-control example. */
public interface Device {
  boolean isOn();

  void setOn(boolean on);

  int volume();

  void setVolume(int volume);
}
// ANCHOR_END: device

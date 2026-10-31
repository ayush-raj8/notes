package patterns.structural.bridge;

// ANCHOR: radio
public final class Radio implements Device {
  private boolean on;
  private int volume = 10;

  @Override
  public boolean isOn() {
    return on;
  }

  @Override
  public void setOn(boolean on) {
    this.on = on;
  }

  @Override
  public int volume() {
    return volume;
  }

  @Override
  public void setVolume(int volume) {
    this.volume = Math.max(0, Math.min(20, volume));
  }
}
// ANCHOR_END: radio

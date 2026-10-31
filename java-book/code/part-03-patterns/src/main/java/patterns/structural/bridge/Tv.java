package patterns.structural.bridge;

// ANCHOR: tv
public final class Tv implements Device {
  private boolean on;
  private int volume = 30;

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
    this.volume = Math.max(0, Math.min(100, volume));
  }
}
// ANCHOR_END: tv

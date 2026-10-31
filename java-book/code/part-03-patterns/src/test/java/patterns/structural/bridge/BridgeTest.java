package patterns.structural.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class BridgeTest {
  @Test
  void anyShapeWorksWithAnyRenderer() {
    assertEquals("vector circle r=5", new CircleShape(new VectorRenderer(), 5).draw());
    assertEquals("pixels for circle r=5", new CircleShape(new RasterRenderer(), 5).draw());
    assertEquals("vector rect 2x3", new RectShape(new VectorRenderer(), 2, 3).draw());
    assertEquals("pixels for rect 2x3", new RectShape(new RasterRenderer(), 2, 3).draw());
  }

  @Test
  void nullRendererRejected() {
    assertThrows(NullPointerException.class, () -> new CircleShape(null, 1));
  }

  @Test
  void remoteWorksWithAnyDevice() {
    Remote r = new Remote(new Radio());
    r.power();
    for (int i = 0; i < 5; i++) {
      r.volumeUp();
    }
    assertEquals("on vol=20", r.status());
    AdvancedRemote a = new AdvancedRemote(new Tv());
    a.mute();
    assertEquals("off vol=0", a.status());
  }
}

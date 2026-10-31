package patterns.creational.singleton;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SingletonTest {
  @Test
  void dclReturnsOneInstanceUnderContention() {
    Set<DclSingleton> seen = ConcurrentHashMap.newKeySet();
    IntStream.range(0, 64).parallel().forEach(i -> seen.add(DclSingleton.getInstance()));
    assertEquals(1, seen.size());
  }

  @Test
  void synchronizedCreatesOnce() {
    SynchronizedSingleton a = SynchronizedSingleton.getInstance();
    assertSame(a, SynchronizedSingleton.getInstance());
    assertEquals(1, SynchronizedSingleton.created());
  }

  @Test
  void plainSerializationBreaksIdentity() {
    SerialSingleton a = SerialSingleton.getInstance();
    assertNotSame(a, SerialUtil.roundTrip(a));
  }

  @Test
  void readResolveAndEnumKeepIdentity() {
    SafeSerialSingleton s = SafeSerialSingleton.getInstance();
    assertSame(s, SerialUtil.roundTrip(s));
    assertSame(EnumSingleton.INSTANCE, SerialUtil.roundTrip(EnumSingleton.INSTANCE));
  }

  @Test
  void cloneBreaksSingleton() {
    CloneableSingleton a = CloneableSingleton.getInstance();
    assertNotSame(a, a.clone());
  }
}

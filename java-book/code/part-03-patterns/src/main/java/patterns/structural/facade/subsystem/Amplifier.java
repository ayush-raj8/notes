package patterns.structural.facade.subsystem;

// ANCHOR: facade-amplifier
/** Package-private: clients outside this package cannot even name it. */
class Amplifier {
  String on() {
    return "amp on";
  }

  String volume(int level) {
    return "amp volume=" + level;
  }

  String off() {
    return "amp off";
  }
}
// ANCHOR_END: facade-amplifier

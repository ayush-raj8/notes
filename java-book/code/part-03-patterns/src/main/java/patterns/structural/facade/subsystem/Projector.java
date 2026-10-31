package patterns.structural.facade.subsystem;

// ANCHOR: facade-projector
class Projector {
  String on() {
    return "projector on";
  }

  String wide() {
    return "projector widescreen";
  }

  String off() {
    return "projector off";
  }
}
// ANCHOR_END: facade-projector

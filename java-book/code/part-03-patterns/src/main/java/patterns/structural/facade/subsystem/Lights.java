package patterns.structural.facade.subsystem;

// ANCHOR: facade-lights
class Lights {
  String dim(int percent) {
    return "lights " + percent + "%";
  }
}
// ANCHOR_END: facade-lights

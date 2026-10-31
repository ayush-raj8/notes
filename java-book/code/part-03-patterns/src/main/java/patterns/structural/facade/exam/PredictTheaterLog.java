package patterns.structural.facade.exam;

import java.util.List;
import patterns.structural.facade.subsystem.HomeTheaterFacade;

// ANCHOR: predict-theater-log
public final class PredictTheaterLog {
  public static void main(String[] args) {
    HomeTheaterFacade f = new HomeTheaterFacade();
    List<String> start = f.watchMovie("X");
    System.out.println(start.size());
    System.out.println(start.get(0));
    System.out.println(start.get(start.size() - 1));
    System.out.println(f.endMovie().get(2));
  }
}
// ANCHOR_END: predict-theater-log

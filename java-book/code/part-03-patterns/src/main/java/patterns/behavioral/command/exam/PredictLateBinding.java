package patterns.behavioral.command.exam;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: predict-late-binding
public final class PredictLateBinding {
  public static void main(String[] args) {
    StringBuilder sb = new StringBuilder("x");
    int[] box = {1};
    List<Runnable> cmds = new ArrayList<>();
    cmds.add(() -> System.out.println("sb=" + sb));
    cmds.add(() -> System.out.println("box=" + box[0]));
    for (int i = 0; i < 2; i++) {
      int copy = i;
      cmds.add(() -> System.out.println("i=" + copy));
    }
    sb.append("y");
    box[0]++;
    cmds.forEach(Runnable::run);
  }
}
// ANCHOR_END: predict-late-binding

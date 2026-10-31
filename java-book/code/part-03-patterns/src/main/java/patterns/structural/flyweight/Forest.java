package patterns.structural.flyweight;

import java.util.ArrayList;
import java.util.List;

// ANCHOR: forest
public final class Forest {
  private final List<Tree> trees = new ArrayList<>();

  public void plant(int x, int y, String name, String color) {
    trees.add(new Tree(x, y, TreeFactory.get(name, color)));
  }

  public int size() {
    return trees.size();
  }

  public List<String> drawAll() {
    return trees.stream().map(Tree::draw).toList();
  }
}
// ANCHOR_END: forest

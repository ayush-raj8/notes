package oop.coll;

import java.util.ArrayDeque;
import java.util.Queue;

public final class CollQueueIndexBroken {
  public static void main(String[] args) {
    Queue<String> q = new ArrayDeque<>();
    q.offer("a");
    q.push("b");
    System.out.println(q.get(0));
  }
}

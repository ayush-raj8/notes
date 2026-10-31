package lld.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

// ANCHOR: lru-cache
/** Least recently used: HashMap for O(1) lookup + doubly linked list ordered by recency. */
public final class LruCache<K, V> implements Cache<K, V> {
  private final class Node {
    final K key;
    V value;
    Node prev;
    Node next;

    Node(K key, V value) {
      this.key = key;
      this.value = value;
    }
  }

  private final int capacity;
  private final Map<K, Node> index = new HashMap<>();
  private final Node head = new Node(null, null); // most recent after head
  private final Node tail = new Node(null, null); // least recent before tail

  public LruCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be positive");
    }
    this.capacity = capacity;
    head.next = tail;
    tail.prev = head;
  }

  @Override
  public Optional<V> get(K key) {
    Node n = index.get(Objects.requireNonNull(key));
    if (n == null) {
      return Optional.empty();
    }
    unlink(n);
    linkFirst(n);
    return Optional.of(n.value);
  }

  @Override
  public void put(K key, V value) {
    Objects.requireNonNull(key);
    Objects.requireNonNull(value);
    Node n = index.get(key);
    if (n != null) {
      n.value = value;
      unlink(n);
      linkFirst(n);
      return;
    }
    if (index.size() == capacity) {
      Node victim = tail.prev;
      unlink(victim);
      index.remove(victim.key);
    }
    n = new Node(key, value);
    index.put(key, n);
    linkFirst(n);
  }

  private void unlink(Node n) {
    n.prev.next = n.next;
    n.next.prev = n.prev;
  }

  private void linkFirst(Node n) {
    n.next = head.next;
    n.prev = head;
    head.next.prev = n;
    head.next = n;
  }

  @Override
  public int size() {
    return index.size();
  }

  @Override
  public int capacity() {
    return capacity;
  }
}
// ANCHOR_END: lru-cache

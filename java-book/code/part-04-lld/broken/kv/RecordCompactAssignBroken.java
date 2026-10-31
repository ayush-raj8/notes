public class RecordCompactAssignBroken {
  record Pair(String key, int value) {
    Pair {
      this.value = Math.max(value, 0);
    }
  }
}

package lld.scheduler;

// ANCHOR: task-status
public enum TaskStatus {
  SCHEDULED,
  RUNNING,
  COMPLETED,
  CANCELLED,
  FAILED;

  public boolean isTerminal() {
    return this == COMPLETED || this == CANCELLED || this == FAILED;
  }
}
// ANCHOR_END: task-status

package lld.scheduler;

// ANCHOR: job
/** Unit of work. Unlike {@link Runnable}, it may throw a checked exception. */
@FunctionalInterface
public interface Job {
  void run() throws Exception;
}
// ANCHOR_END: job

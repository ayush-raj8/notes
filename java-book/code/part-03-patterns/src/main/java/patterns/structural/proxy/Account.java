package patterns.structural.proxy;

// ANCHOR: proxy-account
public interface Account {
  int balance();

  void withdraw(int amount);
}
// ANCHOR_END: proxy-account

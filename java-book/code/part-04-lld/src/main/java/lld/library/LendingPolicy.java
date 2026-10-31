package lld.library;

public record LendingPolicy(int maxLoans, int loanDays) {
  public LendingPolicy {
    if (maxLoans < 1 || loanDays < 1) {
      throw new IllegalArgumentException("maxLoans and loanDays must be >= 1");
    }
  }

  public static LendingPolicy standard() {
    return new LendingPolicy(3, 14);
  }
}

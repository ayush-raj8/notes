package lld.atm;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// ANCHOR: atm
/** One machine, one session at a time. Every public method is synchronized and state-checked. */
public final class Atm {
  static final int MAX_PIN_ATTEMPTS = 3;
  static final int MIN_NOTE = 100;

  private final Bank bank;
  private final CashDispenser cash;
  private final Set<String> blocked = new HashSet<>();
  private AtmState state = AtmState.IDLE;
  private String card;
  private int attempts;

  public Atm(Bank bank, CashDispenser cash) {
    this.bank = bank;
    this.cash = cash;
  }

  private void require(Operation op) {
    if (!state.allows(op)) {
      throw new IllegalStateException(op + " not allowed in " + state);
    }
  }

  public synchronized void insertCard(String cardNumber) {
    require(Operation.INSERT_CARD);
    if (blocked.contains(cardNumber)) {
      throw new IllegalStateException("card is blocked");
    }
    card = cardNumber;
    attempts = 0;
    state = AtmState.CARD_INSERTED;
  }

  public synchronized PinResult enterPin(String pin) {
    require(Operation.ENTER_PIN);
    if (bank.verifyPin(card, pin)) {
      state = AtmState.AUTHENTICATED;
      return PinResult.OK;
    }
    attempts++;
    if (attempts >= MAX_PIN_ATTEMPTS) {
      blocked.add(card);
      reset();
      return PinResult.CARD_BLOCKED;
    }
    return PinResult.WRONG_PIN;
  }

  public synchronized long balance() {
    require(Operation.BALANCE);
    return bank.balance(card);
  }

  /** Order matters: plan the notes, debit the account, then hand out the notes. */
  public synchronized Map<Denomination, Integer> withdraw(int amount)
      throws InsufficientFundsException, CashUnavailableException {
    require(Operation.WITHDRAW);
    if (amount <= 0 || amount % MIN_NOTE != 0) {
      throw new IllegalArgumentException("amount must be a positive multiple of " + MIN_NOTE);
    }
    Map<Denomination, Integer> plan =
        cash.plan(amount).orElseThrow(() -> new CashUnavailableException(amount));
    bank.debit(card, amount);
    cash.dispense(plan);
    return plan;
  }

  public synchronized void deposit(long amount) {
    require(Operation.DEPOSIT);
    if (amount <= 0) {
      throw new IllegalArgumentException("amount must be positive");
    }
    bank.credit(card, amount);
  }

  public synchronized void eject() {
    require(Operation.EJECT);
    reset();
  }

  public synchronized AtmState state() {
    return state;
  }

  private void reset() {
    card = null;
    attempts = 0;
    state = AtmState.IDLE;
  }
}
// ANCHOR_END: atm

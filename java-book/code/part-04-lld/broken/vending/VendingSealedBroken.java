import lld.vending.Coin;
import lld.vending.SelectResult;
import lld.vending.VendingMachine;
import lld.vending.VendingState;
import java.util.List;

public class VendingSealedBroken implements VendingState {
  public void insert(VendingMachine m, Coin c) {}
  public SelectResult select(VendingMachine m, String code) { return null; }
  public List<Coin> cancel(VendingMachine m) { return List.of(); }
}

import lld.parking.FeePolicy;

public class ParkingMissingMethodBroken implements FeePolicy {
  public static void main(String[] args) {
    System.out.println("never runs");
  }
}

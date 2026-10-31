package solid.principles.solutions;

/** Exercise 3: replace trip.driver().license().number() with one delegating method. */
public final class SolutionDemeterDelegate {
  record License(String number) {}

  record Driver(License license) {
    String licenseNumber() {
      return license == null ? "none" : license.number();
    }
  }

  record Trip(Driver driver) {
    String driverLicense() {
      return driver.licenseNumber();
    }
  }

  public static void main(String[] args) {
    System.out.println(new Trip(new Driver(new License("DL-42"))).driverLicense());
    System.out.println(new Trip(new Driver(null)).driverLicense());
  }
}

interface Filter {
  String handle(String request);
}

public final class ChainLambdaNoReturnBroken {
  public static void main(String[] args) {
    Filter f =
        r -> {
          System.out.println("filtering " + r);
        };
    System.out.println(f.handle("x"));
  }
}

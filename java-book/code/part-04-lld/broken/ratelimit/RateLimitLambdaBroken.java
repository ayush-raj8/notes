import lld.ratelimit.RateLimiter;

public class RateLimitLambdaBroken {
  public static void main(String[] args) {
    RateLimiter always = () -> true;
    System.out.println(always.tryAcquire("k"));
  }
}

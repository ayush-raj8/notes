import lld.ratelimit.LimiterConfig;

public class RateLimitRecordFieldBroken {
  public static void main(String[] args) {
    LimiterConfig cfg = new LimiterConfig(3, 1000);
    cfg.limit = 5;
    System.out.println(cfg);
  }
}

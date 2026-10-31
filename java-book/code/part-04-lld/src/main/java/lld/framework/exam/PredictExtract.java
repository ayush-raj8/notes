package lld.framework.exam;

import java.util.List;
import lld.framework.NounVerbExtractor;
import lld.framework.Requirement;

// ANCHOR: predict-extract
public class PredictExtract {
  public static void main(String[] args) {
    var c = NounVerbExtractor.extract(List.of(
        Requirement.functional("driver accepts Ride"),
        Requirement.outOfScope("Surge pricing"),
        Requirement.functional("Rider cancels Ride")));
    System.out.println(c.classes());
    System.out.println(c.methods());
  }
}
// ANCHOR_END: predict-extract

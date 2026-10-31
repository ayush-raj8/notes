package lld.framework.exam;

import lld.framework.InterviewStep;

// ANCHOR: predict-next-step
public class PredictNextStep {
  public static void main(String[] args) {
    InterviewStep s = InterviewStep.DESIGN_APIS;
    System.out.println(s.next().orElseThrow());
    System.out.println(InterviewStep.EXTEND_AND_CONCURRENCY.next().isPresent());
    System.out.println(InterviewStep.totalMinutes());
  }
}
// ANCHOR_END: predict-next-step

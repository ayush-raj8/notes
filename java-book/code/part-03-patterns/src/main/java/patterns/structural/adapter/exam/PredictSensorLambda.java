package patterns.structural.adapter.exam;

import patterns.structural.adapter.FahrenheitSensor;

// ANCHOR: predict-sensor-lambda
public final class PredictSensorLambda {
  public static void main(String[] args) {
    for (double f : new double[] {212, 32, -40, 50}) {
      System.out.println(FahrenheitSensor.adapt(new FahrenheitSensor(f)).celsius());
    }
  }
}
// ANCHOR_END: predict-sensor-lambda

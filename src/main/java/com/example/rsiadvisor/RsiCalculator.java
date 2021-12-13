package com.example.rsiadvisor;

import java.util.List;

public class RsiCalculator {


    public static double calculate(List<Double> data) {
        int periodLength = 14;
        int lastBar = data.size() - 2;
        int firstBar = lastBar - periodLength + 1;


        double aveGain = 0, aveLoss = 0;

        for (int bar = firstBar; bar <= lastBar; bar++) {
            double change = data.get(bar) - data.get(bar - 1);
            if (change >= 0) {
                aveGain += change;
            } else {
                aveLoss += change;
            }
        }
        double averageGain= aveGain/periodLength, averageLoss=aveLoss/periodLength;
       // double rs = aveGain / Math.abs(aveLoss);
       double rs = averageGain / Math.abs(averageLoss);
        double rsi = 100 - 100 / (1 + rs);
//double rsi= rsi1-3;
        return rsi;
    }
}

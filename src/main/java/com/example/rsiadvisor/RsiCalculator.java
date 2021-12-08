package com.example.rsiadvisor;

import java.util.List;

public class RsiCalculator {


    public static double calculate(List<Double> data) {
        int periodLength = 14;
        int lastBar = data.size() - 1;
        int firstBar = lastBar - periodLength + 1;


        double aveGain = 0, aveLoss = 0;
        for (int bar = firstBar + 1; bar <= lastBar; bar++) {
            double change = data.get(bar) - data.get(bar - 1);
            if (change >= 0) {
                aveGain += change;
            } else {
                aveLoss += change;
            }
        }

        double rs = aveGain / Math.abs(aveLoss);
        double rsi = 100 - 100 / (1 + rs);

        return rsi;
    }
}

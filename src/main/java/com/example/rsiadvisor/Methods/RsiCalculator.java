package com.example.rsiadvisor.Methods;

import java.util.List;

public class RsiCalculator {


    public static double calculate(List<Double> data) {
        int periodLength = 14;
        int lastBar = data.size() - 2;
        int firstBar = lastBar - periodLength+1 ;


        double sumGain = 0, sumLoss = 0;

        for (int bar = firstBar; bar <= lastBar; bar++) {
            double change = data.get(bar) - data.get(bar - 1);
            if (change >= 0) {
                sumGain += change;
            } else {
                sumLoss += change;
            }
        }
        double averageGain=sumGain/periodLength,averageLoss=sumLoss/periodLength;
       double rs = averageGain/ Math.abs(averageLoss);

        double rsi = 100 - 100 / (1 + rs);

        return rsi;
    }
}

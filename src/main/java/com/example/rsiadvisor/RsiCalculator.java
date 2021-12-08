package com.example.rsiadvisor;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RsiCalculator {

    public static void main(String[] args) {
        List<Double>list=new ArrayList<>();
        //list.add(559.13);
       list.add(591.44);
       list.add(581.40);
       list.add(603.73);
       list.add(585.15);
       list.add(600.34);
       list.add(611.37);
       list.add(624.31);
       list.add(622.67);
       list.add(627.97);
       list.add(619.47);
       list.add(594.64);
       list.add(569.08);
       list.add(557.78);
       list.add(589.26);

        System.out.println(list);
        calculate(list);
    }
    public static void calculate(List<Double> data) {
        int periodLength = 14;
        int lastBar = data.size()-1 ;
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

        System.out.println("RSI: " + rsi);
    }
}

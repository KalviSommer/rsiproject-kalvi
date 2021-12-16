package com.example.rsiadvisor.Methods;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class Rsi {

    public static double getRsi(String symbol,String timeframe) {
        String mySecret = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1hcnRsYWFuc2FsdUBnbWFpbC5jb20i" +
                "LCJpYXQiOjE2MzkzOTI5MTQsImV4cCI6Nzk0NjU5MjkxNH0.ETkXOxvh6V_J-LnEXZuLeF9qQYiDY8l6tU91zi4ksK0";

        RestTemplate restTemplateTaapio = new RestTemplate();
        ResponseEntity<Object> responseEntityTaapio = restTemplateTaapio.getForEntity
                ("https://api.taapi.io/rsi?secret=" + mySecret + "&exchange=binance&symbol="
                        +symbol + "/USDT&interval=" + timeframe+ "&backtrack=1", Object.class);

        String rsiString = responseEntityTaapio.getBody().toString().substring(7, 13);
        double rsiDouble = Double.parseDouble(rsiString);
        return rsiDouble;
    }






















//    public static double calculate(List<Double> data) {
//        int periodLength = 14;
//        int lastBar = data.size() - 2;
//        int firstBar = lastBar - periodLength+1 ;
//
//
//        double sumGain = 0, sumLoss = 0;
//
//        for (int bar = firstBar; bar <= lastBar; bar++) {
//            double change = data.get(bar) - data.get(bar - 1);
//            if (change >= 0) {
//                sumGain += change;
//            } else {
//                sumLoss += change;
//            }
//        }
//        double averageGain=sumGain/periodLength,averageLoss=sumLoss/periodLength;
//       double rs = averageGain/ Math.abs(averageLoss);
//
//        double rsi = 100 - 100 / (1 + rs);
//
//        return rsi;
//    }
}

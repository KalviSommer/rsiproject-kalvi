package com.example.rsiadvisor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CandleData {

    public static void calculate(List<Double> data) {
        int periodLength = 14;
        int lastBar = data.size() - 1;
        int firstBar = lastBar - periodLength+1;


        double aveGain = 0, aveLoss = 0;
        for (int bar = firstBar+1 ; bar <= lastBar; bar++) {
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


    //@Scheduled(fixedDelay = 1000)
    @EventListener(ApplicationReadyEvent.class)
    public List<Double> dataTest() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3/klines?interval=1d&startTime=1637625600000&endTime=1638835200000&symbol=BNBUSDT", List.class);
        //System.out.println(responseEntity.getBody());
        List elements = responseEntity.getBody();
        List<Double> closeHistory = new ArrayList<>();
        for (Object element : elements) {
            List sublist = (List) element;

           closeHistory.add(Double.parseDouble(sublist.get(4).toString())); // pmst teeb stringist double

        }


        System.out.println(closeHistory);
        calculate(closeHistory);
        return closeHistory;
    }


}


package com.example.rsiadvisor;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.List;

@Component
public class CandleData {



    //@Scheduled(fixedDelay = 1000)
    //@EventListener(ApplicationReadyEvent.class)
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
        System.out.println(RsiCalculator.calculate(closeHistory));
        return closeHistory;
    }


}


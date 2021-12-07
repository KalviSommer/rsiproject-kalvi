package com.example.rsiadvisor;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Component
public class CandleData {

    @Scheduled(fixedDelay = 1000)
    public void dataTest() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3/klines?symbol=BTCUSDT&interval=1h&startTime=1638813600000",List.class);
        System.out.println(responseEntity.getBody());
        List elements =responseEntity.getBody();
        for(Object element: elements) {
            List sublist = (List) element;
            System.out.println(sublist.get(0) + ":" + sublist.get(4));
        }
    }


}

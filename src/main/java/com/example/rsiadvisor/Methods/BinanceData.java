package com.example.rsiadvisor.Methods;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;


public class BinanceData {

    public static List binanceData(String symbol, long timeInMillis) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3" +
                "/klines?interval=1h&symbol=" + symbol + "&startTime=" + timeInMillis, List.class);

        List elements = responseEntity.getBody();
        return elements;
    }


}

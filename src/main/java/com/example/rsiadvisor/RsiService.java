package com.example.rsiadvisor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class RsiService {

    @Autowired
    private RsiRepository rsiRepository;

    public String createNewUser(UsersDto newUser) { //ennem public String createAccount(@PathVariable("accountNr") String accountNr )
        Integer a = rsiRepository.createNewUser(newUser);
        return "New user is created and the user id is: " + a;
    }

    //@Scheduled(fixedDelay = 1000)
    //@EventListener(ApplicationReadyEvent.class)
    public void addRsiDataDailyBtc() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3/klines?interval=1d&startTime=1637625600000&endTime=1638835200000&symbol=BNBUSDT", List.class);

        List elements = responseEntity.getBody();
        List<Double> closeHistory = new ArrayList<>();
        for (Object element : elements) {
            List sublist = (List) element;

            closeHistory.add(Double.parseDouble(sublist.get(4).toString())); // pmst teeb stringist double
        }
        RsiDto btcData=new RsiDto();
        btcData.setRsi(RsiCalculator.calculate(closeHistory));
        btcData.setSymbol("BTCUSDT");
        btcData.setEndDate("siia tuleb currentUpdateDate");
        btcData.setClosingPrice(closeHistory.get(closeHistory.size()-1));
        btcData.setSymbolId(2);

        rsiRepository.addRsiData(btcData);
//        System.out.println(closeHistory);
//        System.out.println(RsiCalculator.calculate(closeHistory));

    }


    //@EventListener(ApplicationReadyEvent.class)
    public void  sendAlarmEmail()   {
        List<Integer>UserId = rsiRepository.getAllUserRsiComparisonBtc();





    }




}

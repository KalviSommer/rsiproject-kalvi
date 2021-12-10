package com.example.rsiadvisor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        LocalDateTime dateTime = LocalDateTime.now();
        String date = dateTime.toString();
        RsiDto btcData = new RsiDto();
        btcData.setRsi(RsiCalculator.calculate(closeHistory));
        btcData.setSymbol("BTCUSDT");

        btcData.setEndDate(date);
        btcData.setClosingPrice(closeHistory.get(closeHistory.size() - 1));
        btcData.setSymbolId(1);


        rsiRepository.addRsiData(btcData);
//        System.out.println(closeHistory);
//        System.out.println(RsiCalculator.calculate(closeHistory));
    }



    @EventListener(ApplicationReadyEvent.class)
    public void sendAlarmEmail() throws MessagingException {
        List<Integer> userId = rsiRepository.getAllUserRsiComparisonBtc();
        for (int i = 0; i < userId.size(); i++) {
            Email.send(rsiRepository.getUserEmail(userId.get(i)), "lalala", "lalalala");
            rsiRepository.deleteUserAlarmBtc(userId.get(i));
        }


    }


    public UsersDto getUser(int id) {
        return rsiRepository.getUser(id);
    }


}

package com.example.rsiadvisor;


import com.example.rsiadvisor.Dto.*;
import com.example.rsiadvisor.Error.ApplicationException;
import com.example.rsiadvisor.Methods.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class RsiService {

    @Autowired
    private RsiRepository rsiRepository;

    public Integer createNewUser(UsersDto newUser) throws MessagingException { //ennem public String createAccount(@PathVariable("accountNr") String accountNr )
        Integer userId = rsiRepository.createNewUser(newUser);
        Email.send(rsiRepository.getUserEmail(userId), "Welcome to RSI Advisor", "Welcome, " + rsiRepository.getUserFirstName(userId) + "\n"
                + "\n"
                + "You have made the right choice to start using RSI advisor.\n"
                + "RSI Advisor is simple to use and an efficient way to save Your time\n"
                + "\n"
                + "Please use your id: " + userId + " when logging in."
                + "\n"
                + "\n"
                + "Should you have any questions, then contact us rsiadvisor.info@gmail.com");
        return userId;
    }

    //CURRENT PRICE TO TABLE *****************************************
    //@EventListener(ApplicationReadyEvent.class)
    public void addCurrentPriceTable() {


        RestTemplate currentPrice = new RestTemplate();
        ResponseEntity<Object> responseEntity = currentPrice.getForEntity("https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT", Object.class);

        double priceString = Double.parseDouble(responseEntity.getBody().toString().substring(23, 31));
        System.out.println(priceString);


    }


//DAILY*********************************

    @Scheduled(cron = "10 0 22 ? * * ")           //10 sekund p2rast syda88d iga p2ev,GMT aeg
    //@EventListener(ApplicationReadyEvent.class)
    public void addRsiDataDaily() {
        List<SymbolDto> symbolDataList = rsiRepository.getSymbols(); // symboli tabeli data

        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.of("GMT")).toInstant();
        long timeInMillisNow = instant.toEpochMilli();// hetke aeg
        long timeInMillis16 = timeInMillisNow - 1382400000;// miinus 16 p2eva, sest viimast objekti closeHistory listist ei kasuta


        for (int i = 0; i < symbolDataList.size(); i++) {

            String mySecret = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1hcnRsYWFuc2FsdUBnbWFpbC5jb20iLCJpYXQiOjE2MzkzOTI5MTQsImV4cCI6Nzk0NjU5MjkxNH0.ETkXOxvh6V_J-LnEXZuLeF9qQYiDY8l6tU91zi4ksK0";
            RestTemplate restTemplateTaapio = new RestTemplate();
            ResponseEntity<Object> responseEntityTaapio = restTemplateTaapio.getForEntity("https://api.taapi.io/rsi?secret=" + mySecret + "&exchange=binance&symbol=" + symbolDataList.get(i).getSymbols().substring(0, 3) + "/USDT&interval=1d&backtrack=1", Object.class);
            String rsiString = responseEntityTaapio.getBody().toString().substring(7, 13);
            double rsiDouble = Double.parseDouble(rsiString);


            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3/klines?interval=1d&symbol=" + symbolDataList.get(i).getSymbols() + "&startTime=" + timeInMillis16, List.class);

            List elements = responseEntity.getBody();
            List<Double> closeHistory = new ArrayList<>();
            List<Long> closeTimeHistory = new ArrayList<>();
            for (Object element : elements) {
                List sublist = (List) element;
                closeTimeHistory.add(Long.parseLong(sublist.get(6).toString()));
                closeHistory.add(Double.parseDouble(sublist.get(4).toString())); // pmst teeb stringist double
            }
            Long dateLong = closeTimeHistory.get(closeTimeHistory.size() - 2);    // leian viimase close aja milliseconds

            final DateTimeFormatter formatter =                                   // siin convertin unixi inimloetavaks
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            final long unixTime = dateLong;
            final String date = Instant.ofEpochMilli(unixTime)
                    .atZone(ZoneId.of("GMT"))
                    .format(formatter);

            RsiDto symbolData = new RsiDto(symbolDataList.get(i).getSymbolId(), rsiDouble,
                    date, closeHistory.get(closeHistory.size() - 2), symbolDataList.get(i).getSymbols());

            System.out.println(closeHistory);
            rsiRepository.addRsiDataDaily(symbolData);

        }
    }


    //HOURLY *******************************************************************************************************
    @Scheduled(cron = "10 0 08/1 ? * * ")
    //@EventListener(ApplicationReadyEvent.class)

    public void addRsiDataHourly() {

        List<SymbolDto> symbolDataList = rsiRepository.getSymbols();        // võtan sümbolid tabelist listi

        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.of("GMT")).toInstant();
        long timeInMillisNow = instant.toEpochMilli();                       // hetke aeg
        long timeInMillis16 = timeInMillisNow - 64800000;                   // nii palju minevikku tagasi


        for (int i = 0; i < symbolDataList.size(); i++) {                   //loopime yle symboli listi

            //SIIN KYSIME RSI V22rtuse
            String mySecret = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6Im1hcnRsYWFuc2FsdUBnbWFpbC5jb20iLCJpY" +
                    "XQiOjE2MzkzOTI5MTQsImV4cCI6Nzk0NjU5MjkxNH0.ETkXOxvh6V_J-LnEXZuLeF9qQYiDY8l6tU91zi4ksK0";
            RestTemplate restTemplateTaapio = new RestTemplate();
            ResponseEntity<Object> responseEntityTaapio = restTemplateTaapio.getForEntity
                    ("https://api.taapi.io/rsi?secret=" + mySecret + "&exchange=binance&symbol="
                            + symbolDataList.get(i).getSymbols().substring(0, 3) + "/USDT&interval=1h&backtrack=1", Object.class);
            String rsiString = responseEntityTaapio.getBody().toString().substring(7, 13);
            double rsiDouble = Double.parseDouble(rsiString);

            // SIIN KYSIME K6IK MUU INFO
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List> responseEntity = restTemplate.getForEntity("https://api.binance.com/api/v3/klines?interval=1h&symbol="
                    + symbolDataList.get(i).getSymbols() + "&startTime=" + timeInMillis16, List.class);

            List elements = responseEntity.getBody();
            List<Double> closeHistory = new ArrayList<>();
            List<Long> closeTimeHistory = new ArrayList<>();
            for (Object element : elements) {
                List sublist = (List) element;
                closeTimeHistory.add(Long.parseLong(sublist.get(6).toString()));
                closeHistory.add(Double.parseDouble(sublist.get(4).toString())); // teeb stringist double
            }
            Long dateLong = closeTimeHistory.get(closeTimeHistory.size() - 2);    // leian viimase close aja milliseconds

            final DateTimeFormatter formatter =                                   // siin convertin unixi close aja inimloetavaks
                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            final long unixTime = dateLong;
            final String date = Instant.ofEpochMilli(unixTime)
                    .atZone(ZoneId.of("GMT+2"))
                    .format(formatter);
            //KOGU INFO PANEME DTO sisse ja saadame tabelisse

            RsiDto symbolData = new RsiDto(symbolDataList.get(i).getSymbolId(), rsiDouble, date,
                    closeHistory.get(closeHistory.size() - 2), symbolDataList.get(i).getSymbols());

            rsiRepository.addRsiDataHourly(symbolData);

        }

    }

    //SEND DAILY ALARM**********************************************************************************************

    @Scheduled(cron = "30 0 22 ? * * ")                  // iga p2ev p2rast syda88d 30 sekundit teeb kontrolli,GMT
    @EventListener(ApplicationReadyEvent.class)
    public void SendAlarmEmailDaily() throws MessagingException {

        List<SymbolDto> symbolList = rsiRepository.getSymbols();
        for (int j = 0; j < symbolList.size(); j++) {

            List<Integer> userId = rsiRepository.getAllUserRsiComparisonDaily(symbolList.get(j).getSymbolId());

            for (int i = 0; i < userId.size(); i++) {
                Email.send(rsiRepository.getUserEmail(userId.get(i)), symbolList.get(j).getSymbols() + " daily ", "Your Daily timeframe " + symbolList.get(j).getSymbols() + " alarm was triggered.");
                rsiRepository.deleteUserAlarm(userId.get(i), symbolList.get(j).getSymbolId(), "1D", " rsi_daily ");
            }
        }
    }
    //SEND HOURLY ALARM**********************************************************************************************

    @Scheduled(cron = "30 0 08/1 ? * * ")                  // iga tund ja 30 sekundit GMT , teeb kontrolli
    @EventListener(ApplicationReadyEvent.class)
    public void SendAlarmEmailHourly() throws MessagingException {

        List<SymbolDto> symbolList = rsiRepository.getSymbols();
        for (int j = 0; j < symbolList.size(); j++) {


            List<Integer> userId = rsiRepository.getAllUserRsiComparisonHourly(symbolList.get(j).getSymbolId());

            for (int i = 0; i < userId.size(); i++) {
                Email.send(rsiRepository.getUserEmail(userId.get(i)), symbolList.get(j).getSymbols() + "  hourly ", "Your hourly timeframe " + symbolList.get(j).getSymbols() + " alarm was triggered.");
                rsiRepository.deleteUserAlarm(userId.get(i), symbolList.get(j).getSymbolId(), "1H", " rsi_hourly ");
            }
        }
    }


    public UsersDto getUser(int id) {
        return rsiRepository.getUser(id);
    }


    public void setAlert(int symbolId, int userId, int rsiFilter, String rsiTimeframe) throws MessagingException {
        if (rsiFilter < 1 || rsiFilter > 100) {
            throw new ApplicationException("Rsi filter should be 1 => 100!");
        }

        if (rsiRepository.checkUserAlarm(symbolId, userId, rsiFilter, rsiTimeframe) < 1) {

            rsiRepository.setAlert(symbolId, userId, rsiFilter, rsiTimeframe);
            Email.send(rsiRepository.getUserEmail(userId), "Notification",
                    rsiRepository.getUserFirstName(userId) + ", inserted new alert by details: symbol= " + symbolId +
                            ", rsi filter= " +
                            rsiFilter + ", rsi timeframe= " + rsiTimeframe + "!");

        } else {
            throw new ApplicationException("Alarm already exists!");
        }

    }


    public List<AlertDto> alertList(int userId) {
        List<AlertDto> fullAlertList = new ArrayList<>();
        // TODO, küsi kõik user_symbolid kus user_id = userId
        // Tee foreach tskükkel üle nende kõigi
        List<UserSymbolDto> userSymbols = rsiRepository.getUserSymbols(userId);
        // tsükli sees küsi kõige viimased andmed rsi tabelist
        for (UserSymbolDto userSymbol : userSymbols) {
            if(userSymbol.getRsiTimeframe().equals("1D")){
                RsiDto rsiData = rsiRepository.getRsiDailyLatest(userSymbol.getSymbolId());
                AlertDto alert = new AlertDto();
                alert.setRsi(rsiData.getRsi());
                alert.setClosingPrice(rsiData.getClosingPrice());
                alert.setId(userSymbol.getId());
                alert.setSymbol(rsiData.getSymbol());
                alert.setRsiFilter(userSymbol.getRsiFilter());
                alert.setRsiTimeframe("1D");
                fullAlertList.add(alert);
            } else {
                RsiDto rsiData = rsiRepository.getRsiHourlyLatest(userSymbol.getSymbolId());
                AlertDto alert = new AlertDto();
                alert.setRsi(rsiData.getRsi());
                alert.setClosingPrice(rsiData.getClosingPrice());
                alert.setId(userSymbol.getId());
                alert.setSymbol(rsiData.getSymbol());
                alert.setRsiFilter(userSymbol.getRsiFilter());
                alert.setRsiTimeframe("1H");
                fullAlertList.add(alert);
            }
        }
       // fullAlertList.addAll(rsiRepository.alertList(userId, "1D", "rsi_daily"));
       // fullAlertList.addAll(rsiRepository.alertList(userId, "1H", "rsi_hourly"));
        return fullAlertList;
    }

    public void deleteAlert(int n) {
        rsiRepository.deleteAlert(n);
    }


}

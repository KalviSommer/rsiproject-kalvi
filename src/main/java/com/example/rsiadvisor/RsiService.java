package com.example.rsiadvisor;


import com.example.rsiadvisor.Dto.*;
import com.example.rsiadvisor.Error.ApplicationException;
import com.example.rsiadvisor.Methods.BinanceData;
import com.example.rsiadvisor.Methods.Email;
import com.example.rsiadvisor.Methods.Rsi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    public Integer createNewUser(UsersDto newUser) throws MessagingException {
        Integer userId = rsiRepository.createNewUser(newUser);
        Email.send(rsiRepository.getUserEmail(userId), "Welcome to RSI Advisor", "Welcome, "
                + rsiRepository.getUserFirstName(userId) + "\n"
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


    //ADD DATA TO RSI TABLES********************************************************************************************
    @Scheduled(cron = "10 0 08/1 ? * * ")
    @EventListener(ApplicationReadyEvent.class)

    public void addRsiData() {

        List<RsiTableDto> timeframeData = new ArrayList<>();            // 1D ja 1H timeframe

        RsiTableDto daily = new RsiTableDto("1d", 1382400000, "rsi_daily");
        RsiTableDto hourly = new RsiTableDto("1h", 64800000, "rsi_hourly");

        timeframeData.add(daily);
        timeframeData.add(hourly);

        List<SymbolDto> symbolDataList = rsiRepository.getSymbols();        // võtan sümbolid tabelist listi


        for (int j = 0; j < timeframeData.size(); j++) {


            LocalDateTime localDateTime = LocalDateTime.now();
            Instant instant = localDateTime.atZone(ZoneId.of("GMT")).toInstant();
            long timeInMillisNow = instant.toEpochMilli();
            long timeInMillis = timeInMillisNow - timeframeData.get(j).getTimeMillis();


            for (int i = 0; i < symbolDataList.size(); i++) {                   //loopime yle symboli listi

                //SIIN KYSIME RSI V22rtuse

                double rsiDouble = Rsi.getRsi(symbolDataList.get(i).getSymbols().substring(0, 3),
                        timeframeData.get(j).getTimeframe());

                // SIIN KYSIME K6IK MUU INFO

                List elements = BinanceData.binanceData(symbolDataList.get(i).getSymbols(), timeInMillis);

                List<Double> closeHistory = new ArrayList<>();
                List<Long> closeTimeHistory = new ArrayList<>();
                for (Object element : elements) {
                    List sublist = (List) element;
                    closeTimeHistory.add(Long.parseLong(sublist.get(6).toString()));
                    closeHistory.add(Double.parseDouble(sublist.get(4).toString())); // teeb stringist double
                }
                Long dateLong = closeTimeHistory.get(closeTimeHistory.size() - 2);    // viimane close aeg milliseconds

                final DateTimeFormatter formatter =                                   // unix aeg inimloetavaks
                        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                final long unixTime = dateLong;
                final String date = Instant.ofEpochMilli(unixTime)
                        .atZone(ZoneId.of("GMT+2"))
                        .format(formatter);
                //KOGU INFO PANEME DTO sisse ja saadame tabelisse

                RsiDto symbolData = new RsiDto(symbolDataList.get(i).getSymbolId(), rsiDouble, date,
                        closeHistory.get(closeHistory.size() - 2), symbolDataList.get(i).getSymbols());

                rsiRepository.addRsiDataToTable(symbolData, timeframeData.get(j).getTableName());

            }
        }

    }


    //SEND ALARM**********************************************************************************************

    @Scheduled(cron = "30 0 08/1 ? * * ")                  // iga tund ja 30 sekundit GMT , teeb kontrolli
    @EventListener(ApplicationReadyEvent.class)
    public void SendAlarmEmail() throws MessagingException {

        List<AlarmEmailDto> timeframeData = new ArrayList<>();

        AlarmEmailDto daily = new AlarmEmailDto("1D", "rsi_daily", "Your daily timeframe ");
        AlarmEmailDto hourly = new AlarmEmailDto("1H", "rsi_hourly", "Your hourly timeframe ");

        timeframeData.add(hourly);
        timeframeData.add(daily);


        List<CrossingDto> crossing = new ArrayList<>();

        CrossingDto crossingUp = new CrossingDto("<", "crossing up");
        CrossingDto crossingDown = new CrossingDto(">", "crossing down");

        crossing.add(crossingDown);
        crossing.add(crossingUp);

        List<SymbolDto> symbolList = rsiRepository.getSymbols();

        for (int l = 0; l < timeframeData.size(); l++) {

            for (int k = 0; k < crossing.size(); k++) {

                for (int j = 0; j < symbolList.size(); j++) {

                    List<Integer> userId = rsiRepository.getAllUserRsiComparison(symbolList.get(j).getSymbolId(),
                            timeframeData.get(l).getTimeFrame(), timeframeData.get(l).getTableName(), crossing.get(k)
                                    .getCrossing());

                    for (int i = 0; i < userId.size(); i++) {

                        Email.send(rsiRepository.getUserEmail(userId.get(i)), symbolList.get(j).getSymbols()
                                + " " + timeframeData.get(l).getTimeFrame(), timeframeData.get(l).getBody() + " "
                                + symbolList.get(j).getSymbols() + " " +
                                crossing.get(k).getName() + " alarm was triggered.");

                        rsiRepository.deleteUserAlarm(userId.get(i), symbolList.get(j).getSymbolId(), timeframeData
                                .get(l).getTimeFrame(), timeframeData.get(l).getTableName(), crossing.get(k)
                                .getCrossing());
                    }
                }
            }
        }
    }


    public UsersDto getUser(int id) {
        return rsiRepository.getUser(id);
    }


    public void setAlert(int symbolId, int userId, int rsiFilter, String rsiTimeframe, String crossing)
            throws MessagingException {
        if (rsiFilter < 1 || rsiFilter > 100) {
            throw new ApplicationException("Rsi filter should be 1 => 100!");
        }

        if (rsiRepository.checkUserAlarm(symbolId, userId, rsiFilter, rsiTimeframe, crossing) < 1) {

            rsiRepository.setAlert(symbolId, userId, rsiFilter, rsiTimeframe, crossing);
            Email.send(rsiRepository.getUserEmail(userId), "Notification",
                    rsiRepository.getUserFirstName(userId) + ", inserted new alert by details: symbol= "
                            + symbolId + ", rsi filter= " + rsiFilter + ", rsi timeframe= " + rsiTimeframe + "!");

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
            if (userSymbol.getRsiTimeframe().equals("1D")) {
                RsiDto rsiData = rsiRepository.getRsiDailyLatest(userSymbol.getSymbolId());
                AlertDto alert = new AlertDto();
                alert.setRsi(rsiData.getRsi());
                alert.setClosingPrice(rsiData.getClosingPrice());
                alert.setId(userSymbol.getId());
                alert.setSymbol(rsiData.getSymbol());
                alert.setRsiFilter(userSymbol.getRsiFilter());
                alert.setRsiTimeframe("1D");
                alert.setCrossing(userSymbol.getCrossing());
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
                alert.setCrossing(userSymbol.getCrossing());
                fullAlertList.add(alert);
            }
        }

        return fullAlertList;
    }


    public void deleteAlert(int n) {
        rsiRepository.deleteAlert(n);
    }


}

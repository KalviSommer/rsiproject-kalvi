package com.example.rsiadvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RsiRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public Integer createNewUser(UsersDto newUser) {
        String sql = "INSERT INTO users(first_name, last_name, email) VALUES (:firstName, :lastName, :email)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("firstName", newUser.getFirstName());
        paramMap.put("lastName", newUser.getLastName());
        paramMap.put("email", newUser.getEmail());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql, new MapSqlParameterSource(paramMap), keyHolder);
        return (Integer) keyHolder.getKeys().get("user_id");
    }

    public void addRsiDataDaily(RsiDto rsi) {
        String sql = "INSERT INTO rsi_daily(symbol,end_date,closing_price,rsi,symbol_id) VALUES (:symbol, :endDate, :closingPrice,:rsi,:symbolId)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbol", rsi.getSymbol());
        paramMap.put("endDate", rsi.getEndDate());
        paramMap.put("closingPrice", rsi.getClosingPrice());
        paramMap.put("rsi", rsi.getRsi());
        paramMap.put("symbolId", rsi.getSymbolId());

        jdbcTemplate.update(sql, paramMap);

    }

    public void addRsiDataHourly(RsiDto rsi) {
        String sql = "INSERT INTO rsi_hourly(symbol,end_date,closing_price,rsi,symbol_id) VALUES (:symbol, :endDate, :closingPrice,:rsi,:symbolId)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbol", rsi.getSymbol());
        paramMap.put("endDate", rsi.getEndDate());
        paramMap.put("closingPrice", rsi.getClosingPrice());
        paramMap.put("rsi", rsi.getRsi());
        paramMap.put("symbolId", rsi.getSymbolId());

        jdbcTemplate.update(sql, paramMap);

    }

    public UserSymbolDto getUserSymbolData(int userId, int symbolId) {
        String sql = "SELECT *FROM user_symbol WHERE user_id = :userId AND symbol_id=:symbolId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("symbolId", symbolId);
        UserSymbolDto result = jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(UserSymbolDto.class));
        return result;

    }


    public RsiDto getRsiDailyLatest(int symbolId) {
        String sql = "SELECT*FROM rsi_daily WHERE symbol_id = :symbolId ORDER BY row_id desc LIMIT 1";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        RsiDto result = jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(RsiDto.class));
        return result;
    }

    public UsersDto getUser(int id) {
        String sql = "SELECT * FROM users WHERE user_id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(UsersDto.class));
    }


// ALARM COMPARSION DAILY***************************************************************************
    public List<Integer> getAllUserRsiComparisonDaily(int symbolId) {    // TAGASTAB LISTI USER ID KELLEL ALARM L2KS K2ima
        String sql = "SELECT user_id FROM user_symbol WHERE symbol_id = :symbolId AND rsi_timeframe='1D' AND\n" +
                "                rsi_filter > (SELECT rsi FROM rsi_daily WHERE symbol_id = 1 ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        return jdbcTemplate.queryForList(sql, paramMap, Integer.class);

    }
    // ALARM COMPARSION HOURLY
    public List<Integer> getAllUserRsiComparisonHourly(int symbolId) {    // TAGASTAB LISTI USER ID KELLEL ALARM L2KS K2ima
        String sql = "SELECT user_id FROM user_symbol WHERE symbol_id = :symbolId AND rsi_timeframe='1H' AND\n" +
                "                rsi_filter > (SELECT rsi FROM rsi_hourly WHERE symbol_id = 1 ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        return jdbcTemplate.queryForList(sql, paramMap, Integer.class);
    }





    public String getUserEmail(int id) {
        String sql = "SELECT email FROM users WHERE user_id=:id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);


    }

    public String getUserFirstName(int id) {
        String sql = "SELECT first_name FROM users WHERE user_id=:id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);
    }
    //OTSIB KAS TABELIS ON JUBA SAMA ALARM
    public int checkUserAlarm(int symbolId,int userId,int rsiFilter,String rsiTimeframe) {
        String sql = "SELECT id FROM user_symbol WHERE symbol_id=:symbolId AND user_id=:userId AND rsi_filter=:rsiFilter AND rsi_timeframe=:rsiTimeframe";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        paramMap.put("userId", userId);
        paramMap.put("rsiFilter", rsiFilter);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

// Tsyklilise kontrolli alarmi kustutamine
    public void deleteUserAlarm(int userId,int symbolId,String rsiTimeframe,String rsiTable) {
        String sql = "DELETE FROM user_symbol WHERE symbol_id = :symbolId AND user_id=:userId AND rsi_timeframe=:rsiTimeframe AND rsi_filter > (SELECT rsi FROM" + rsiTable + " WHERE symbol_id = :symbolId ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("symbolId", symbolId);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        paramMap.put("rsiTable", rsiTable);

        jdbcTemplate.update(sql, paramMap);
    }



    public void setAlert(int symbolId, int userId, int rsiFilter, String rsiTimeframe) {

        String sql = "INSERT INTO user_symbol (symbol_id, user_id, rsi_filter, rsi_timeframe) " +
                "VALUES (:symbolid, :userid, :rsifilter, :rsitimeframe)";
        Map<String, Object> bankAccountMap = new HashMap<>();
        bankAccountMap.put("symbolid", symbolId);
        bankAccountMap.put("userid", userId);
        bankAccountMap.put("rsifilter", rsiFilter);
        bankAccountMap.put("rsitimeframe", rsiTimeframe);
        jdbcTemplate.update(sql, bankAccountMap);
    }

    public List<AlertDto> alertList(int userId, String timeframe, String tableName) {
        String sql = "WITH all_alerts AS (SELECT *, ROW_NUMBER() OVER(PARTITION BY r.symbol_id, u.rsi_timeframe  ORDER BY r.end_date DESC) AS rn FROM user_symbol u JOIN " + tableName + " r ON u.symbol_id = r.symbol_id WHERE u.user_id=:userId AND u.rsi_timeframe=:rsiTimeframe) select * from all_alerts where rn=1";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("rsiTimeframe", timeframe);
        return jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(AlertDto.class));
    }

    public List<SymbolDto> getSymbols() {
        String sql = "SELECT*FROM symbol";
        Map<String, Object> paramMap = new HashMap<>();
        return jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(SymbolDto.class));
    }

    public void deleteAlert(int n) {
        String sql = "DELETE FROM user_symbol WHERE id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", n);
        jdbcTemplate.update(sql, paramMap);
    }



    public void updateUserAlarm(int symbolId, int userId, int rsiFilter, String rsiTimeframe) {
        String sql = "UPDATE user_symbol SET rsi_filter=:rsiFilter WHERE symbol_id=:symbolId AND user_id=:userId AND rsi_timeframe=:rsiTimeframe";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("rsiFilter", rsiFilter);
        paramMap.put("symbolId", symbolId);
        paramMap.put("userId", userId);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        jdbcTemplate.update(sql, paramMap);
    }

}



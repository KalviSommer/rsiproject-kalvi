package com.example.rsiadvisor;

import com.example.rsiadvisor.Dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

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

    public void addRsiDataToTable(RsiDto rsi, String tableName) {
        String sql = "INSERT INTO " + tableName + "(symbol,end_date,closing_price,rsi,symbol_id) VALUES" +
                " (:symbol, :endDate, :closingPrice,:rsi,:symbolId)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbol", rsi.getSymbol());
        paramMap.put("endDate", rsi.getEndDate());
        paramMap.put("closingPrice", rsi.getClosingPrice());
        paramMap.put("rsi", rsi.getRsi());
        paramMap.put("symbolId", rsi.getSymbolId());

        jdbcTemplate.update(sql, paramMap);

    }


    public List<UserSymbolDto> getUserSymbols(int userId) {
        String sql = "SELECT *FROM user_symbol WHERE user_id = :userId";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        List<UserSymbolDto> result = jdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<>(UserSymbolDto.class));
        return result;

    }


    public RsiDto getRsiDailyLatest(int symbolId) {
        String sql = "SELECT*FROM rsi_daily WHERE symbol_id = :symbolId ORDER BY row_id desc LIMIT 1";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        RsiDto result = jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(RsiDto.class));
        return result;
    }

    public RsiDto getRsiHourlyLatest(int symbolId) {
        String sql = "SELECT*FROM rsi_hourly WHERE symbol_id = :symbolId ORDER BY row_id desc LIMIT 1";
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


    // COMPARE LATEST RSI VALUE AND USER ALARM***************************************************************************
    public List<Integer> getAllUserRsiComparison(int symbolId, String timeFrame, String tableName, String crossing) {
        String sql = "SELECT user_id FROM user_symbol WHERE symbol_id = :symbolId AND rsi_timeframe=:timeFrame AND" +
                " crossing=:crossing AND rsi_filter " + crossing + " (SELECT rsi FROM " + tableName + " WHERE" +
                " symbol_id =:symbolId ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        paramMap.put("timeFrame", timeFrame);
        paramMap.put("crossing", crossing);
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
    public int checkUserAlarm(int symbolId, int userId, int rsiFilter, String rsiTimeframe, String crossing) {
        String sql = "SELECT COUNT(*) FROM user_symbol WHERE symbol_id=:symbolId AND user_id=:userId AND" +
                " rsi_filter=:rsiFilter AND rsi_timeframe=:rsiTimeframe AND crossing=:crossing";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolId", symbolId);
        paramMap.put("userId", userId);
        paramMap.put("rsiFilter", rsiFilter);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        paramMap.put("crossing", crossing);
        return jdbcTemplate.queryForObject(sql, paramMap, Integer.class);

    }

    // Tsyklilise kontrolli alarmi kustutamine
    public void deleteUserAlarm(int userId, int symbolId, String rsiTimeframe, String rsiTable, String crossing) {
        String sql = "DELETE FROM user_symbol WHERE symbol_id = :symbolId AND user_id=:userId AND" +
                " rsi_timeframe=:rsiTimeframe AND rsi_filter " + crossing + " (SELECT rsi FROM " + rsiTable + " WHERE" +
                " symbol_id = :symbolId ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        paramMap.put("symbolId", symbolId);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        paramMap.put("rsiTable", rsiTable);

        jdbcTemplate.update(sql, paramMap);
    }


    public void setAlert(int symbolId, int userId, int rsiFilter, String rsiTimeframe, String crossing) {

        String sql = "INSERT INTO user_symbol (symbol_id, user_id, rsi_filter, rsi_timeframe, crossing) " +
                "VALUES (:symbolid, :userid, :rsifilter, :rsitimeframe,:crossing)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("symbolid", symbolId);
        paramMap.put("userid", userId);
        paramMap.put("rsifilter", rsiFilter);
        paramMap.put("rsitimeframe", rsiTimeframe);
        paramMap.put("crossing", crossing);
        jdbcTemplate.update(sql, paramMap);
    }

    public List<AlertDto> alertList(int userId, String timeframe, String tableName) {
        String sql = "WITH all_alerts AS (SELECT *, ROW_NUMBER() OVER(PARTITION BY r.symbol_id, u.rsi_timeframe" +
                "  ORDER BY r.end_date DESC) AS rn FROM user_symbol u JOIN " + tableName + " r ON" +
                " u.symbol_id = r.symbol_id WHERE u.user_id=:userId AND u.rsi_timeframe=:rsiTimeframe)" +
                " select * from all_alerts where rn=1";
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
        String sql = "UPDATE user_symbol SET rsi_filter=:rsiFilter WHERE symbol_id=:symbolId AND user_id=:userId AND" +
                " rsi_timeframe=:rsiTimeframe";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("rsiFilter", rsiFilter);
        paramMap.put("symbolId", symbolId);
        paramMap.put("userId", userId);
        paramMap.put("rsiTimeframe", rsiTimeframe);
        jdbcTemplate.update(sql, paramMap);
    }

}



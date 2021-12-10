package com.example.rsiadvisor;

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

    public void addRsiData(RsiDto rsi) {
        String sql = "INSERT INTO rsi_daily(symbol,end_date,closing_price,rsi,symbol_id) VALUES (:symbol, :endDate, :closingPrice,:rsi,:symbolId)";
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

    public List<Integer> getAllUserRsiComparisonBtc() {    // TAGASTAB LISTI USER ID KELLEL ALARM L2KS K2ima
        String sql = "SELECT user_id FROM user_symbol WHERE symbol_id = 1 AND\n" +
                "                rsi_filter < (SELECT rsi FROM rsi_daily WHERE symbol_id = 1 ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        return jdbcTemplate.queryForList(sql, paramMap, Integer.class);

    }

    public String getUserEmail(int id){
        String sql = "SELECT email FROM users WHERE user_id=:id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, String.class);


    }
    public void deleteUserAlarmBtc(int userId) {
        String sql = "DELETE FROM user_symbol WHERE symbol_id = 1 AND user_id=:userId AND\n" +
                "                rsi_filter < (SELECT rsi FROM rsi_daily WHERE symbol_id = 1 ORDER BY row_id desc LIMIT 1)";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        jdbcTemplate.update(sql, paramMap);


    }
}



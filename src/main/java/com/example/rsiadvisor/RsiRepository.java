package com.example.rsiadvisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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

        jdbcTemplate.update(sql,paramMap);

    }

    public UsersDto getUser(int id) {
        String sql = "SELECT * FROM users WHERE user_id = :id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("id", id);
        return jdbcTemplate.queryForObject(sql, paramMap, new BeanPropertyRowMapper<>(UsersDto.class));
    }



}



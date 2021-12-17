package com.example.rsiadvisor.dto;

public class RsiTableDto {

    private String timeframe;
    private long timeMillis;
    private String tableName;

    public RsiTableDto(String timeframe, long timeMillis, String tableName) {
        this.timeframe = timeframe;
        this.timeMillis = timeMillis;
        this.tableName = tableName;
    }

    public RsiTableDto() {
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}

package com.example.rsiadvisor.Dto;

public class RsiTableDto {

    private String timeframe;
    private int timeMillis;
    private String tableName;

    public RsiTableDto() {
    }

    public RsiTableDto(String timeframe, int timeMillis, String tableName) {
        this.timeframe = timeframe;
        this.timeMillis = timeMillis;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public int getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(int timeMillis) {
        this.timeMillis = timeMillis;
    }
}

package com.example.rsiadvisor.Dto;

public class AlarmEmailDto {
    private String timeFrame;
    private String tableName;
    private String body;

    public AlarmEmailDto() {
    }

    public AlarmEmailDto(String timeFrame, String tableName, String body) {
        this.timeFrame = timeFrame;
        this.tableName = tableName;
        this.body = body;
    }

    public String getTimeFrame() {
        return timeFrame;
    }

    public void setTimeFrame(String timeFrame) {
        this.timeFrame = timeFrame;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

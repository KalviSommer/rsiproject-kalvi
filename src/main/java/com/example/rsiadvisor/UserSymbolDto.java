package com.example.rsiadvisor;

public class UserSymbolDto {
    private int symbolId;
    private int userId;
    private int rsiFilter;

    public int getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(int symbolId) {
        this.symbolId = symbolId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRsiFilter() {
        return rsiFilter;
    }

    public void setRsiFilter(int rsiFilter) {
        this.rsiFilter = rsiFilter;
    }
}

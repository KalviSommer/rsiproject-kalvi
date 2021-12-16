package com.example.rsiadvisor.Dto;

public class AlertDto {
    private int id;
    private String symbol;
    private double closingPrice;
    private double rsi;
    private int rsiFilter;
    private String rsiTimeframe;
    private String crossing;

    public String getCrossing() {
        return crossing;
    }

    public void setCrossing(String crossing) {
        this.crossing = crossing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(double closingPrice) {
        this.closingPrice = closingPrice;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public int getRsiFilter() {
        return rsiFilter;
    }

    public void setRsiFilter(int rsiFilter) {
        this.rsiFilter = rsiFilter;
    }

    public String getRsiTimeframe() {
        return rsiTimeframe;
    }

    public void setRsiTimeframe(String rsiTimeframe) {
        this.rsiTimeframe = rsiTimeframe;
    }
}

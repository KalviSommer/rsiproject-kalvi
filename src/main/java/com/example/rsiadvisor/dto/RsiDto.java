package com.example.rsiadvisor.dto;

public class RsiDto {

private int symbolId;
private double rsi;
private String endDate;
private double closingPrice;
private int rowId;
private String symbol;
private String crossing;

    public String getCrossing() {
        return crossing;
    }

    public void setCrossing(String crossing) {
        this.crossing = crossing;
    }

    public RsiDto() {
    }

    public RsiDto(int symbolId, double rsi, String endDate, double closingPrice, String symbol) {
        this.symbolId = symbolId;
        this.rsi = rsi;
        this.endDate = endDate;
        this.closingPrice = closingPrice;
        this.symbol = symbol;
    }





    public int getSymbolId() {
        return symbolId;
    }

    public void setSymbolId(int symbolId) {
        this.symbolId = symbolId;
    }

    public double getRsi() {
        return rsi;
    }

    public void setRsi(double rsi) {
        this.rsi = rsi;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getClosingPrice() {
        return closingPrice;
    }

    public void setClosingPrice(double closingPrice) {
        this.closingPrice = closingPrice;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}

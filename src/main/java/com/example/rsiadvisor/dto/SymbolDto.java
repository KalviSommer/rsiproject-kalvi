package com.example.rsiadvisor.dto;

public class SymbolDto {

    private int symbolId;
    private String symbols;

    public int getSymbolId() {
        return symbolId;
    }

    @Override
    public String toString() {
        return "SymbolDto{" +
                "symbolId=" + symbolId +
                ", symbol='" + symbols + '\'' +
                '}';
    }

    public void setSymbolId(int symbolId) {
        this.symbolId = symbolId;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }
}

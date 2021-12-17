package com.example.rsiadvisor.dto;

public class CrossingDto {
    private String crossing;
    private String name;

    public CrossingDto() {
    }

    public CrossingDto(String crossing, String name) {
        this.crossing = crossing;
        this.name = name;
    }

    public String getCrossing() {
        return crossing;
    }

    public void setCrossing(String crossing) {
        this.crossing = crossing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

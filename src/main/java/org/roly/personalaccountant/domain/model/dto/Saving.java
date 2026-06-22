package org.roly.personalaccountant.domain.model.dto;

public class Saving {

    private Long id;
    private String name;
    private double percentage;
    private double value;

    public Saving(Long id, String name, double percentage) {
        this.id = id;
        this.name = name;
        this.percentage = percentage;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void calculateValue(double earnings) {
        this.value = earnings * this.percentage / 100;
    }

    public double getValue() {
        return value;
    }
}

package org.roly.personalaccountant.domain.model.dto;

public class Percentages {

    private double corePercentage;
    private double needPercentage;
    private double savePercentage;

    public double getCorePercentage() {
        return corePercentage;
    }

    public void setCorePercentage(double corePercentage) {
        this.corePercentage = corePercentage;
    }

    public double getWantPercentage() {
        return needPercentage;
    }

    public void setWantPercentage(double wantPercentage) {
        this.needPercentage = wantPercentage;
    }

    public double getSavePercentage() {
        return savePercentage;
    }

    public void setSavePercentage(double savePercentage) {
        this.savePercentage = savePercentage;
    }
}

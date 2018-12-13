package com.predrika.icha.autobiblio;

public class Fines {
    private String loanId;
    private String titleFines;
    private int damageCost;
    private int overdueCost;
    private int totalCost;
    private int paidAmount;
    private String paidOff;

    public Fines(){

    }

    public Fines(String loanId, String titleFines, int damageCost, int overdueCost, int totalCost, int paidAmount, String paidOff) {
        this.loanId = loanId;
        this.titleFines = titleFines;
        this.damageCost = damageCost;
        this.overdueCost = overdueCost;
        this.totalCost = totalCost;
        this.paidAmount = paidAmount;
        this.paidOff = paidOff;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getTitleFines() {
        return titleFines;
    }

    public void setTitleFines(String titleFines) {
        this.titleFines = titleFines;
    }

    public int getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(int damageCost) {
        this.damageCost = damageCost;
    }

    public int getOverdueCost() {
        return overdueCost;
    }

    public void setOverdueCost(int overdueCost) {
        this.overdueCost = overdueCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(int totalCost) {
        this.totalCost = totalCost;
    }

    public int getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(int paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidOff() {
        return paidOff;
    }

    public void setPaidOff(String paidOff) {
        this.paidOff = paidOff;
    }
}

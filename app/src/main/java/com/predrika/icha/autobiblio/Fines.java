package com.predrika.icha.autobiblio;

import java.math.BigDecimal;

public class Fines {
    private String loanId;
    private String titleFines;
    private String bookIdFines;
    private double damageCost;
    private double overdueCost;
    private double totalCost;
    private double paidAmount;
    private String paidOff;
    private String uid;

    public Fines(){

    }

    public Fines(String loanId, String titleFines, String bookIdFines, double damageCost, double overdueCost, double totalCost, double paidAmount, String paidOff, String uid) {
        this.loanId = loanId;
        this.titleFines = titleFines;
        this.bookIdFines = bookIdFines;
        this.damageCost = damageCost;
        this.overdueCost = overdueCost;
        this.totalCost = totalCost;
        this.paidAmount = paidAmount;
        this.paidOff = paidOff;
        this.uid = uid;
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

    public String getBookIdFines() {
        return bookIdFines;
    }

    public void setBookIdFines(String bookIdFines) {
        this.bookIdFines = bookIdFines;
    }

    public double getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(double damageCost) {
        this.damageCost = damageCost;
    }

    public double getOverdueCost() {
        return overdueCost;
    }

    public void setOverdueCost(double overdueCost) {
        this.overdueCost = overdueCost;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPaidOff() {
        return paidOff;
    }

    public void setPaidOff(String paidOff) {
        this.paidOff = paidOff;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

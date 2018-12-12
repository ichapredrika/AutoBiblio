package com.predrika.icha.autobiblio;

public class Fines {
    private String loanId;
    private String title;
    private Double damageCost;
    private Double overdueCost;
    private Double total;
    private Double paidAmount;
    private Boolean paidOffYN;

    public Fines(){

    }
    public Fines(String loanId, String title, Double damageCost, Double overdueCost, Double total, Double paidAmount, Boolean paidOffYN) {
        this.loanId = loanId;
        this.title = title;
        this.damageCost = damageCost;
        this.overdueCost = overdueCost;
        this.total = total;
        this.paidAmount = paidAmount;
        this.paidOffYN = paidOffYN;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getDamageCost() {
        return damageCost;
    }

    public void setDamageCost(Double damageCost) {
        this.damageCost = damageCost;
    }

    public Double getOverdueCost() {
        return overdueCost;
    }

    public void setOverdueCost(Double overdueCost) {
        this.overdueCost = overdueCost;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public Boolean getPaidOffYN() {
        return paidOffYN;
    }

    public void setPaidOffYN(Boolean paidOffYN) {
        this.paidOffYN = paidOffYN;
    }
}

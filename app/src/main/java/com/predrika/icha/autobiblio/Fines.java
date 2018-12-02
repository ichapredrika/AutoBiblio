package com.predrika.icha.autobiblio;

public class Fines {
    private String loanId;
    private Double damageCost;
    private Double overdueCost;
    private Double total;
    private Double paidAmount;
    private Boolean paidOffYN;

    public String getLoanId() {
        return loanId;
    }

    public Double getDamageCost() {
        return damageCost;
    }

    public Double getOverdueCost() {
        return overdueCost;
    }

    public Double getTotal() {
        return total;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public Boolean getPaidOffYN() {
        return paidOffYN;
    }
}

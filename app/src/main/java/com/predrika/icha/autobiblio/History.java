package com.predrika.icha.autobiblio;

public class History {
    private String loanId;
    private String bookId;
    private String dateIssued;
    private String returnedDate;
    private Boolean damagedYN;
    private Boolean overdueYN;
    private Boolean paidOffYN;

    public String getLoanId() {
        return loanId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public Boolean getDamagedYN() {
        return damagedYN;
    }

    public Boolean getOverdueYN() {
        return overdueYN;
    }

    public Boolean getPaidOffYN() {
        return paidOffYN;
    }
}

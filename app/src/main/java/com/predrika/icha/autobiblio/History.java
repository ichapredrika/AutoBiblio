package com.predrika.icha.autobiblio;

public class History {
    private String loanId;
    private String bookId;
    private String title;
    private String issuedDate;
    private String returnedDate;
    private Boolean damagedYN;
    private Boolean overdueYN;
    private Boolean paidOffYN;

    public History(){

    }

    public History(String loanId, String bookId, String title, String issuedDate, String returnedDate, Boolean damagedYN, Boolean overdueYN, Boolean paidOffYN) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.title = title;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.damagedYN = damagedYN;
        this.overdueYN = overdueYN;
        this.paidOffYN = paidOffYN;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

    public Boolean getDamagedYN() {
        return damagedYN;
    }

    public void setDamagedYN(Boolean damagedYN) {
        this.damagedYN = damagedYN;
    }

    public Boolean getOverdueYN() {
        return overdueYN;
    }

    public void setOverdueYN(Boolean overdueYN) {
        this.overdueYN = overdueYN;
    }

    public Boolean getPaidOffYN() {
        return paidOffYN;
    }

    public void setPaidOffYN(Boolean paidOffYN) {
        this.paidOffYN = paidOffYN;
    }
}

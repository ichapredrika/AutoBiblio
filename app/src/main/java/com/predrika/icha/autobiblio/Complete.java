package com.predrika.icha.autobiblio;

public class Complete {
    private String loanId;
    private String bookId;
    private String titleComplete;
    private String issuedDateComplete;
    private String returnedDate;
    private String damagedYN;
    private String overdueYN;
    private String paidOffYN;

    public Complete(){

    }

    public Complete(String loanId, String bookId, String titleComplete, String issuedDateComplete, String returnedDate, String damagedYN, String overdueYN, String paidOffYN) {
        this.loanId = loanId;
        this.bookId = bookId;
        this.titleComplete = titleComplete;
        this.issuedDateComplete = issuedDateComplete;
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

    public String getTitleComplete() {
        return titleComplete;
    }

    public void setTitleComplete(String titleComplete) {
        this.titleComplete = titleComplete;
    }

    public String getIssuedDateComplete() {
        return issuedDateComplete;
    }

    public void setIssuedDateComplete(String issuedDateComplete) {
        this.issuedDateComplete = issuedDateComplete;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }

    public String getDamagedYN() {
        return damagedYN;
    }

    public void setDamagedYN(String damagedYN) {
        this.damagedYN = damagedYN;
    }

    public String getOverdueYN() {
        return overdueYN;
    }

    public void setOverdueYN(String overdueYN) {
        this.overdueYN = overdueYN;
    }

    public String getPaidOffYN() {
        return paidOffYN;
    }

    public void setPaidOffYN(String paidOffYN) {
        this.paidOffYN = paidOffYN;
    }
}

package com.predrika.icha.autobiblio;

public class Complete {
    private String loanId;
    private String bookIdComplete;
    private String titleComplete;
    private String issuedDateComplete;
    private String returnedDate;
    private String damagedYN;
    private String overdueYN;
    private String paidOffYN;
    private String uid;

    public Complete(){

    }

    public Complete(String loanId, String bookIdComplete, String titleComplete, String issuedDateComplete, String returnedDate, String damagedYN, String overdueYN, String paidOffYN, String uid) {
        this.loanId = loanId;
        this.bookIdComplete = bookIdComplete;
        this.titleComplete = titleComplete;
        this.issuedDateComplete = issuedDateComplete;
        this.returnedDate = returnedDate;
        this.damagedYN = damagedYN;
        this.overdueYN = overdueYN;
        this.paidOffYN = paidOffYN;
        this.uid = uid;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getBookIdComplete() {
        return bookIdComplete;
    }

    public void setBookIdComplete(String bookIdComplete) {
        this.bookIdComplete = bookIdComplete;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

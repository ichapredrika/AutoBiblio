package com.predrika.icha.autobiblio;

public class OnGoing {
    private String loanId;
    private String title;
    private String bookIdOnGoing;
    private String issuedDate;
    private String maxReturnDate;
    private String uid;
    private String borrowQR;

    public OnGoing(){

    }

    public OnGoing(String loanId, String title, String bookIdOnGoing, String issuedDate, String maxReturnDate, String uid, String borrowQR) {
        this.loanId = loanId;
        this.title = title;
        this.bookIdOnGoing = bookIdOnGoing;
        this.issuedDate = issuedDate;
        this.maxReturnDate = maxReturnDate;
        this.uid = uid;
        this.borrowQR = borrowQR;
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

    public String getBookIdOnGoing() {
        return bookIdOnGoing;
    }

    public void setBookIdOnGoing(String bookIdOnGoing) {
        this.bookIdOnGoing = bookIdOnGoing;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getMaxReturnDate() {
        return maxReturnDate;
    }

    public void setMaxReturnDate(String maxReturnDate) {
        this.maxReturnDate = maxReturnDate;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBorrowQR() {
        return borrowQR;
    }

    public void setBorrowQR(String borrowQR) {
        this.borrowQR = borrowQR;
    }
}

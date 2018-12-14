package com.predrika.icha.autobiblio;

public class OnGoing {
    private String loanId;
    private String title;
    private String bookId;
    private String issuedDate;
    private String maxReturnDate;
    private String uid;

    public OnGoing(){

    }

    public OnGoing(String loanId, String title, String bookId, String issuedDate, String maxReturnDate, String uid) {
        this.loanId = loanId;
        this.title = title;
        this.bookId = bookId;
        this.issuedDate = issuedDate;
        this.maxReturnDate = maxReturnDate;
        this.uid = uid;
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

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
}

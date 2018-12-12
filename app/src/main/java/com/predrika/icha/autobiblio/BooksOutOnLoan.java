package com.predrika.icha.autobiblio;

public class BooksOutOnLoan {
    //userid
    private String loanId;
    private String title;
    private String bookId;
    private String issuedDate;
    private String maxReturnDate;

    public BooksOutOnLoan() {

    }

    public BooksOutOnLoan(String loanId, String title, String bookId, String issuedDate, String maxReturnDate) {
        this.loanId = loanId;
        this.title = title;
        this.bookId = bookId;
        this.issuedDate = issuedDate;
        this.maxReturnDate = maxReturnDate;
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
}
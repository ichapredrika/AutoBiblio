package com.predrika.icha.autobiblio;

public class BooksOutOnLoan {
    //userid
    private String loanId;
    private String bookId;
    private String dateIssued;
    private String maxReturnDate;

    public String getLoanId() {
        return loanId;
    }

    public String getBookId() {
        return bookId;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public String getMaxReturnDate() {
        return maxReturnDate;
    }
}

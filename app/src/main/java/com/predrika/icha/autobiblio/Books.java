package com.predrika.icha.autobiblio;

public class Books {
    private String bookId;
    private String isbn;
    private String availability;

    public Books(){
    }

    public Books(String bookId, String isbn, String availability) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.availability = availability;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}

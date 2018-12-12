package com.predrika.icha.autobiblio;

public class BooksSpecification {
    private String isbn;
    private String bookId;
    private String genre;
    private String bookType;
    private String author;
    private String publisher;
    private String bookPrice;
    private String shelf;
    private String title;
    private String image;

    public BooksSpecification(){

    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(String bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public BooksSpecification(String isbn, String bookId, String genre, String bookType, String author, String publisher, String bookPrice, String shelf, String title, String image) {
        this.isbn = isbn;
        this.bookId = bookId;
        this.genre = genre;
        this.bookType = bookType;
        this.author = author;
        this.publisher = publisher;
        this.bookPrice = bookPrice;
        this.shelf = shelf;
        this.title = title;
        this.image = image;
    }
}


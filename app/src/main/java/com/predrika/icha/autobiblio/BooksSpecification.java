package com.predrika.icha.autobiblio;

public class BooksSpecification {
    private String isbn;
    private String bookId;
    private String genre;
    private String collectionType;
    private String author;
    private String publisher;
    private double bookPrice;
    private String location;
    private String title;
    private String image;

    public BooksSpecification(){

    }

    public BooksSpecification(String isbn, String bookId, String genre, String collectionType, String author, String publisher, double bookPrice, String location, String title, String image) {
        this.isbn = isbn;
        this.bookId = bookId;
        this.genre = genre;
        this.collectionType = collectionType;
        this.author = author;
        this.publisher = publisher;
        this.bookPrice = bookPrice;
        this.location = location;
        this.title = title;
        this.image = image;
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

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
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

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}


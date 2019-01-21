package com.predrika.icha.autobiblio;

public class BorrowQR {
    private String link;
    private String info;
    private int counter;
    private String uid;

    public BorrowQR(){
    }

    public BorrowQR(String link, String info, int counter, String uid) {
        this.link = link;
        this.info = info;
        this.counter = counter;
        this.uid = uid;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

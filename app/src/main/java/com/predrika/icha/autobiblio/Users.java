package com.predrika.icha.autobiblio;

public class Users {
    private String fullName;
    private String univId;
    private String pob;
    private String dob;
    private String address;
    private String phone;
    private String emailAddress;
    private String memberType;
    private double balanceAmount;

    public Users(){

    }

    public Users(String fullName, String univId, String pob, String dob, String address, String phone, String emailAddress, String memberType, double balanceAmount) {
        this.fullName = fullName;
        this.univId = univId;
        this.pob = pob;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.emailAddress = emailAddress;
        this.memberType = memberType;
        this.balanceAmount = balanceAmount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUnivId() {
        return univId;
    }

    public void setUnivId(String univId) {
        this.univId = univId;
    }

    public String getPob() {
        return pob;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }
}

package com.taxapprf.taxapp.usersdata;

public class Transaction {
    private String id, type, date, currency;
    private Double rateCentralBank;
    private Double sum, sumRub;

    public Transaction() {
    }

    public Transaction(String id, String type, String date, String currency, Double sum) {
        this.id = id;
        this.type = type;
        this.date = date;
        this.currency = currency;
        this.sum = sum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getRateCentralBank() {
        return rateCentralBank;
    }

    public void setRateCentralBank(Double rateCentralBank) {
        this.rateCentralBank = rateCentralBank;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getSumRub() {
        return sumRub;
    }

    public void setSumRub(Double sumRub) {
        this.sumRub = sumRub;
    }

    @Override
    public String toString() {
        String s = getType() + getId() + getSum().toString() + getCurrency() + getRateCentralBank().toString() + getSumRub().toString();
        return s;
    }
}

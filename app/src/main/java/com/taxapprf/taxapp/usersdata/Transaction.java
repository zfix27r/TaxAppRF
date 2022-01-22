package com.taxapprf.taxapp.usersdata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Comparable<Transaction>{
    private String key, id, type, date, currency;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    @Override
    public int compareTo(Transaction o) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String date1Str = this.date;
        String date2Str = o.getDate();
        try {
            Date date1 = formatter.parse(date1Str);
            Date date2 = formatter.parse(date2Str);
            if (date1.before(date2)){
                return 1;
            }else if (date1.after(date2)){
                return -1;
            }else return 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

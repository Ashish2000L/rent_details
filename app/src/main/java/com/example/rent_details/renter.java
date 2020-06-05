package com.example.rent_details;

public class renter {

    private String date,amount,unit, rent,bill;

    public renter() {

    }

    public renter(String date, String amount, String unit, String rent, String bill) {
        this.date = date;
        this.amount = amount;
        this.unit = unit;
        this.rent = rent;
        this.bill = bill;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getBill() {
        return bill;
    }

    public void setBill(String bill) {
        this.bill = bill;
    }
}

package com.example.rent_details;



public class renter {

    private String date,amount,unit,rent,bill,addedon,lastupdate,note;


    public renter() {

    }

    public renter( String date, String amount, String unit, String rent, String bill, String addedon, String lastupdate,String note) {

        this.date = date;
        this.amount = amount;
        this.unit = unit;
        this.rent = rent;
        this.bill = bill;
        this.addedon = addedon;
        this.lastupdate = lastupdate;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
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

    public String getAddedon() {
        return addedon;
    }

    public void setAddedon(String addedon) {
        this.addedon = addedon;
    }

    public String getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(String lastupdate) {
        this.lastupdate = lastupdate;
    }
}

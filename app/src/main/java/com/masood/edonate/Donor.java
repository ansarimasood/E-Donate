package com.masood.edonate;

/**
 * Created by Masood on 27-02-2018.
 */

public class Donor {
    public String quantity,address,time,accepted;

    public Donor(){

    }

    Donor(String quantity, String address, String time, String accepted){
        this.quantity=quantity;
        this.address=address;
        this.time= time;
        this.accepted=accepted;
         }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }
}

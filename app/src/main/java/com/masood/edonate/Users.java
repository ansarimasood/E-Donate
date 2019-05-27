package com.masood.edonate;

/**
 * Created by Masood on 05-03-2018.
 */

public class Users {
    String accepted,address,quantity,time;
    public Users(){

    }
    public Users(String accepted,String address,String quantity,String time){
        this.accepted = accepted;
        this.address = address;
        this.quantity = quantity;
        this.time = time;
    }

    public String getAccepted() {
        return accepted;
    }

    public void setAccepted(String accepted) {
        this.accepted = accepted;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

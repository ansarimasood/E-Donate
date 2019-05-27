package com.masood.edonate;

/**
 * Created by Masood on 03-03-2018.
 */

public class NgoAccepted {
    public String quantity,address,time,contact,email,name,acceptedtime;
    public  NgoAccepted(){

    }
    public NgoAccepted(String quantity,String address,String time,String name,String contact,String email,String acceptedtime){
        this.quantity=quantity;
        this.address=address;
        this.time= time;
        this.name=name;
        this.contact=contact;
        this.email=email;
        this.acceptedtime=acceptedtime;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAcceptedtime() {
        return acceptedtime;
    }

    public void setAcceptedtime(String acceptedtime) {
        this.acceptedtime = acceptedtime;
    }
}

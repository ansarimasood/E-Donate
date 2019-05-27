package com.masood.edonate;

/**
 * Created by Masood on 02-03-2018.
 */

public class Ngo {
    public String quantity,address,time,name,contact,email;

    public  Ngo(){

    }
    public Ngo(String quantity,String address,String time,String name,String contact,String email){
        this.quantity=quantity;
        this.address=address;
        this.time= time;
        this.name=name;
        this.contact=contact;
        this.email=email;
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
}

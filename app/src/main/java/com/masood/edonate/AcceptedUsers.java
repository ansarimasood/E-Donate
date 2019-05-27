package com.masood.edonate;

/**
 * Created by Masood on 08-03-2018.
 */

public class AcceptedUsers {
    String acceptedtime,time;
    public AcceptedUsers(){

    }
    public AcceptedUsers(String time ,String acceptedtime){
        this.time = time;
        this.acceptedtime=acceptedtime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAcceptedtime() {
        return acceptedtime;
    }

    public void setAcceptedtime(String acceptedtime) {
        this.acceptedtime = acceptedtime;
    }
}

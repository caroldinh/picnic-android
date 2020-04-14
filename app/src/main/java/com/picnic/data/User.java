package com.picnic.data;

import java.util.ArrayList;

public class User {

    public String UID;
    public String displayName;
    public ArrayList<String> joined;
    public ArrayList<String> hosted;

    public User(String UID, String displayName){
        this.UID = UID;
        joined = new ArrayList<>();
        hosted = new ArrayList<>();
        this.displayName = displayName;
    }


}

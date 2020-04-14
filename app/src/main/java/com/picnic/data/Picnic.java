package com.picnic.data;

import java.util.ArrayList;

public class Picnic {

    public String name;
    public String description;
    public String hostUID;
    public String id;
    public ArrayList<Artwork> artworks;
    public ArrayList<User> members;

    public Picnic(String name, String description, String hostUID, String id){
        this.name = name;
        this.description = description;
        this.hostUID = hostUID;
        this.id = id;
    }

}

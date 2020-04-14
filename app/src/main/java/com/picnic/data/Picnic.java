package com.picnic.data;

import java.util.ArrayList;

public class Picnic {

    public String name;
    public String description;
    public String hostUID;
    public String id;
    public ArrayList<Artwork> artworks;
    public ArrayList<Member> members;

    public Picnic(String name, String description, String hostUID, String id){
        this.name = name;
        this.description = description;
        this.hostUID = hostUID;
        this.id = id;
        members = new ArrayList<>();
        members.add(new Member(hostUID));
        artworks = new ArrayList<>();
    }

}

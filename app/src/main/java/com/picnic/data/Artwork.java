package com.picnic.data;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Artwork {

    public String title;
    public String artist;
    public String imageURL;
    public String description;
    public String feedback;
    public String timestamp;
    public List<Critique> critiques;

    public Artwork(String title, String artist, String imageURL, String description, String feedback){

        this.title = title;
        this.artist = artist;
        this.imageURL = imageURL;
        this.description = description;
        this.feedback = feedback;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.timestamp = timestamp.toString();

        critiques = new ArrayList<>();

    }

    public void addCritique(Critique c){

        critiques.add(c);
    }

}

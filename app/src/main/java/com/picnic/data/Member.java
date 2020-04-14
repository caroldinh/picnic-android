package com.picnic.data;

public class Member {

    public String UID;
    public int contributions;
    public int critiques;

    public Member(String UID){

        this.UID = UID;
        contributions = 0;
        critiques = 0;

    }

    public Member(String UID, int contributions, int critiques){

        this.UID = UID;
        this.contributions = contributions;
        this.critiques = critiques;

    }

    public void addContrib(){
        contributions++;
    }

    public void addCritique(){
        critiques++;
    }

}

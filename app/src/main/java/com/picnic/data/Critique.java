package com.picnic.data;

import java.sql.Timestamp;

public class Critique {

    public String critiquer;
    public String bread1;
    public String sandwich;
    public String bread2;
    public String timestamp;

    public Critique(String critiquer, String bread1, String sandwich, String bread2){
        this.critiquer = critiquer;
        this.bread1 = bread1;
        this.sandwich = sandwich;
        this.bread2 = bread2;

        Timestamp ts = new Timestamp(System.currentTimeMillis());
        this.timestamp = ts.toString();
    }

}

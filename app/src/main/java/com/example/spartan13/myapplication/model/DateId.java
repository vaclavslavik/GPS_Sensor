package com.example.spartan13.myapplication.model;

import java.sql.Date;

/**
 * Created by spartan13 on 14. 2. 2015.
 */
public class DateId {
    private int id;
    private Date date;


    public DateId(int id, Date date) {
        this.id = id;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }
}

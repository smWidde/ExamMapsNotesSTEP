package com.example.exammaps;

import java.io.Serializable;
import java.util.Calendar;

public class Note implements Serializable {
    public int Id;
    public String Title;
    public String Text;
    public Note()
    {
        Id=0;
    }
    public Note(String Title, String Text)
    {
        this.Title = Title;
        this.Text = Text;
        Id=0;
    }
}

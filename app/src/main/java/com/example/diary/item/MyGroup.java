package com.example.diary.item;

import java.util.ArrayList;

public class MyGroup {
    public ArrayList<String> child;
    public String groupName;

    public MyGroup(String name){
        groupName = name;
        child = new ArrayList<String>();
    }
}


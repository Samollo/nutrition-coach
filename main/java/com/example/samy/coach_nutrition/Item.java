package com.example.samy.coach_nutrition;

public class Item {

    String name;
    int image;

    public Item(String name,int image) {
        this.image = image;
        this.name = name;
    }
    public String getName()
    {
        return name;
    }
    public int getImage()
    {
        return image;
    }
}

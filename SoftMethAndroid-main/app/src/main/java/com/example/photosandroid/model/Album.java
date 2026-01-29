package com.example.photosandroid.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Album implements Serializable {

    private String name;
    private ArrayList<Photo> photos = new ArrayList<>();

    public Album(String name){
        this.name = Objects.requireNonNull(name).trim();
    }

    public String getName(){
        return name;
    }

    public ArrayList<Photo> getPhotos(){
        return photos;
    }

    public void addPhoto(Photo p){
        photos.add(p);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name).trim();
    }
}

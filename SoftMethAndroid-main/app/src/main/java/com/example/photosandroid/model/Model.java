package com.example.photosandroid.model;

import android.content.Context;
import android.graphics.Color;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Model implements Serializable {
    private static Model instance;
    public ArrayList<Album> albums = new ArrayList<>();

    public static Model getInstance() {
        if(instance == null){
            instance = new Model();
            //instance.makeFakeData(); //test
        }
        return instance;
    }

    public ArrayList<String> getAlbumsByName(){
        return albums.stream()
                .map(Album::getName)
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private void makeFakeData() {
//        Album a = new Album("Vacation");
//        a.addPhoto(new Photo(Color.RED));
//        a.addPhoto(new Photo(Color.GREEN));
//        a.addPhoto(new Photo(Color.BLUE));
//        for (int i = 0; i < 50; i++) {
//            a.addPhoto(new Photo(Color.rgb(
//                    (int)(Math.random() * 255),
//                    (int)(Math.random() * 255),
//                    (int)(Math.random() * 255)
//            )));
//            a.getPhotos().get(i).addTag("location", "London");
//        }
//
//        Album b = new Album("Family");
//        b.addPhoto(new Photo(Color.MAGENTA));
//        b.addPhoto(new Photo(Color.YELLOW));
//
//        albums.add(a);
//        albums.add(b);
    }

    public void save(Context context) {
        try {
            FileOutputStream fos = context.openFileOutput("albums.dat", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD
    public static void load(Context context) {
        try {
            FileInputStream fis = context.openFileInput("albums.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
            instance = (Model) ois.readObject();
            ois.close();
        } catch (Exception e) {
            instance = new Model();
        }
    }
}

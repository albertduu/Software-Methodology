package com.example.photosandroid;

import android.app.Application;

import com.example.photosandroid.model.Model;

public class PhotosApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Model.load(this);
    }
}


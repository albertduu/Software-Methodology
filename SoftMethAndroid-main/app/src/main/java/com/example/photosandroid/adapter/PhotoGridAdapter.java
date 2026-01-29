package com.example.photosandroid.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.util.ImageLoader;

import java.util.List;

public class PhotoGridAdapter extends BaseAdapter {

    private Context context;
    private List<Photo> photos;
    private boolean selectionMode;
    private List<Integer> selectedPositions;
    public PhotoGridAdapter(Context context, List<Photo> photos) {
        this.context = context;
        this.photos = photos;
    }

    public void setSelectionState(boolean selectionMode, List<Integer> selectedPositions) {
        this.selectionMode = selectionMode;
        this.selectedPositions = selectedPositions;
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int pos) {
        return photos.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.photo_cell, parent, false);
        } else {
            view = convertView;
        }

        ImageView img = view.findViewById(R.id.photo_thumb);

        Uri uri = photos.get(pos).getUri();

        // recommended thumbnail size (grid cell ~130dp)
        int thumbSize = dpToPx(130);

        Bitmap bmp = ImageLoader.loadThumbnail(context, uri, thumbSize);
        if (bmp != null) {
            img.setImageBitmap(bmp);
        }

        // draw selection overlay on the container
        if (selectionMode && selectedPositions.contains(pos)) {
            view.setForeground(new ColorDrawable(0xAA3A7AFE)); // semi-transparent blue
        } else {
            view.setForeground(null);
        }

        return view;
    }

    private int dpToPx(int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

}
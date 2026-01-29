package com.example.photosandroid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.photosandroid.R;
import com.example.photosandroid.controller.MainActivity;
import com.example.photosandroid.model.Album;

import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {

    private Context context;
    private ArrayList<Album> albums;

    public AlbumAdapter(Context context, ArrayList<Album> albums) {
        super(context, 0, albums);
        this.context = context;
        this.albums = albums;
    }

    @NonNull
    @Override
    public View getView(int position,
                        @Nullable View convertView,
                        @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.album_row, parent, false);
        }

        Album album = albums.get(position);

        TextView name = convertView.findViewById(R.id.album_name);
        name.setText(album.getName());

        ImageButton renameBtn = convertView.findViewById(R.id.rename_album);
        ImageButton deleteBtn = convertView.findViewById(R.id.delete_album);

        renameBtn.setOnClickListener(v -> {
            ((MainActivity)context).showRenameDialog(position);
        });

        deleteBtn.setOnClickListener(v -> {
            ((MainActivity)context).showDeleteDialog(position);
        });

        // Clicking the row (not icons) opens the album
        convertView.setOnClickListener(v -> {
            ((MainActivity)context).openAlbum(album.getName(), position);
        });

        return convertView;
    }
}


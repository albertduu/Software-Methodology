package com.example.photosandroid.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photosandroid.R;
import com.example.photosandroid.adapter.PhotoGridAdapter;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Model;
import com.example.photosandroid.model.Photo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ShowAlbum extends AppCompatActivity {
    private boolean selectionMode;
    private GridView photoGrid;
    private LinearLayout selectionBar;
    private ImageButton addPhoto;
    private Button selectButton;
    private Button cancelButton;
    private List<Integer> selectedPhotos = new ArrayList<Integer>();
    private Button deleteButton;
    private Button moveButton;
    private PhotoGridAdapter adapter;
    private Album album;
    private static final int PICK_IMAGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.album);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.album_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar myToolbar = findViewById(R.id.album_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String albumName = bundle.getString("album_name");
        int albumPosition = bundle.getInt("album_position");
        album = Model.getInstance().albums.get(albumPosition);
        ((TextView) findViewById(R.id.album_title)).setText(album.getName());

        photoGrid = findViewById(R.id.photo_grid);




        adapter = new PhotoGridAdapter(this, album.getPhotos());
        photoGrid.setAdapter(adapter);

        addPhoto = findViewById(R.id.add_photo);
        selectionBar = findViewById(R.id.bottom_selection);
        selectButton = findViewById(R.id.select_button);
        cancelButton = findViewById(R.id.cancel_button);
        deleteButton = findViewById(R.id.delete_button);
        moveButton = findViewById(R.id.move_button);
        selectButton.setOnClickListener((View v) -> enterSelectionMode());

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitSelectionMode();
            }
        });

        photoGrid.setOnItemClickListener((parent, view, position, idk) -> {
            if (selectionMode){
                if(selectedPhotos.contains(position)){
                    selectedPhotos.remove(Integer.valueOf(position));
                } else {
                    selectedPhotos.add(position);
                }
                adapter.notifyDataSetChanged();
            }  else {
                openPhoto(albumPosition, position);
            }
        });

        deleteButton.setOnClickListener(v -> showDeleteDialog());
        moveButton.setOnClickListener(v -> showMoveDialog());

        addPhoto.setOnClickListener(v -> openGallery());
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, PICK_IMAGE);
    }
    private void openPhoto(int album_index, int position){
        Bundle bundle = new Bundle();
        bundle.putString("mode", "album");
        bundle.putInt("album_index",album_index);
        bundle.putInt("start_index", position);
        Intent intent = new Intent(this, ShowPhoto.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void enterSelectionMode(){
        selectionMode = true;
        selectedPhotos.clear();
        selectionBar.setVisibility(View.VISIBLE);
        selectButton.setVisibility(View.GONE);
        adapter.setSelectionState(true, selectedPhotos);
        adapter.notifyDataSetChanged();

    }

    private void exitSelectionMode(){
        selectionMode = false;
        selectedPhotos.clear();
        selectionBar.setVisibility(View.GONE);
        selectButton.setVisibility(View.VISIBLE);
        adapter.setSelectionState(false, selectedPhotos);
        adapter.notifyDataSetChanged();
        Model.getInstance().save(this);
    }

    public void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Photos")
                .setMessage("Are you sure you would like to delete the selected photos?")
                .setPositiveButton("Delete", (d,i) -> {
                    deleteSelectedPhotos();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showMoveDialog(){
        if (selectedPhotos.isEmpty()) {
            Toast.makeText(this, "No photos selected", Toast.LENGTH_SHORT).show();
            return;
        }

        // list album names except current one
        ArrayList<Album> all = Model.getInstance().albums;
        ArrayList<String> names = new ArrayList<>();
        for (Album a : all) {
            if (a != album) {
                names.add(a.getName());
            }
        }

        if (names.isEmpty()) {
            Toast.makeText(this, "No other albums available", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Move photos to:")
                .setItems(names.toArray(new String[0]), (dialog, which) -> {
                    // find album by name
                    Album target = null;
                    String chosen = names.get(which);
                    for (Album a : all) {
                        if (a.getName().equals(chosen)) {
                            target = a;
                            break;
                        }
                    }
                    if(target == null) return;
                    performMove(target);
                })
                .show();
    }

    private void deleteSelectedPhotos(){
        selectedPhotos.sort(Comparator.reverseOrder());
        for(int i : selectedPhotos){
            album.getPhotos().remove(i);
        }
        exitSelectionMode();
    }

    private void performMove(Album targetAlbum) {
        ArrayList<Photo> photosToMove = getSelectedPhotos();
        targetAlbum.getPhotos().addAll(photosToMove);
        deleteSelectedPhotos();
        exitSelectionMode();
    }

    private ArrayList<Photo> getSelectedPhotos() {
        ArrayList<Photo> result = new ArrayList<>();
        for (int pos : selectedPhotos) {
            result.add(album.getPhotos().get(pos));
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            final int flags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            getContentResolver().takePersistableUriPermission(imageUri, flags);
            album.getPhotos().add(new Photo(imageUri));
            adapter.notifyDataSetChanged();
            Model.getInstance().save(this);
        }
    }

}
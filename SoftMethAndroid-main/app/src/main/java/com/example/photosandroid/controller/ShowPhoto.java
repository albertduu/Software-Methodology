package com.example.photosandroid.controller;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photosandroid.R;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Model;
import com.example.photosandroid.model.Photo;
import com.example.photosandroid.search.SearchEngine;
import com.example.photosandroid.util.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ShowPhoto extends AppCompatActivity {
    private ImageView photoImage;
    private List<Photo> photoList;
    private int albumIndex;
    private int photoIndex;
    private Button nextButton;
    private Button prevButton;
    private Button addTag;
    private Button removeTag;
    private TextView tagList;
    private Photo p;
    private int screenWidth;
    private int screenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.photo_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.photo_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar myToolbar = findViewById(R.id.photo_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        photoImage = findViewById(R.id.photo_image);
        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        tagList = findViewById(R.id.tag_list);
        addTag = findViewById(R.id.add_tag_button);
        removeTag = findViewById(R.id.delete_tag_button);
        String mode = getIntent().getExtras().getString("mode");
        if(mode.equals("album")){
            albumIndex = getIntent().getExtras().getInt("album_index");
            photoList = Model.getInstance().albums.get(albumIndex).getPhotos();
        } else {
            photoList = SearchEngine.lastResults;
        }
        photoIndex = getIntent().getExtras().getInt("start_index");
        p = photoList.get(photoIndex);

        show_photo();
        showTags();
        prevButton.setOnClickListener(v -> {
            if(photoIndex > 0){
                photoIndex--;
                p = photoList.get(photoIndex);
                show_photo();
            }
        });

        nextButton.setOnClickListener(v -> {
            if(photoIndex < photoList.size() - 1){
                photoIndex++;
                p = photoList.get(photoIndex);
                show_photo();
            }
        });

        addTag.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = getLayoutInflater().inflate(R.layout.add_tag_dialog, null);
            builder.setView(view);

            RadioGroup group = view.findViewById(R.id.tag_type_group);
            EditText valueInput = view.findViewById(R.id.tag_value);

            builder.setPositiveButton("Add", (dialog, which) -> {
                int checkedId = group.getCheckedRadioButtonId();
                String type = (checkedId == R.id.radio_person) ? "person" : "location";

                String value = valueInput.getText().toString().trim().toLowerCase();

                if (!value.isEmpty()) {
                    p.addTag(type,value);
                    Model.getInstance().save(this);
                    showTags();
                }
            });

            builder.setNegativeButton("Cancel", null);

            builder.create().show();
        });

        removeTag.setOnClickListener(v -> {

            String[] types = {"person", "location"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete which type?");
            builder.setItems(types, (d, whichType) -> {
                String type = types[whichType];
                Set<String> set = p.getTags().get(type);

                if (set.isEmpty()) {
                    Toast.makeText(this, "No tags to delete", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] values = set.toArray(new String[0]);

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Delete which value?");
                builder2.setItems(values, (d2, whichValue) -> {
                    String value = values[whichValue];
                    p.removeTag(type, value);
                    Model.getInstance().save(this);
                    showTags();
                });

                builder2.show();

            });
            builder.show();
        });

    }

    private void show_photo(){
        Uri uri = p.getUri();
        int maxSize = Math.max(screenWidth, screenHeight);
        Bitmap bmp = ImageLoader.loadThumbnail(this, uri, maxSize);
        photoImage.setImageBitmap(bmp);
        showTags();

        int num_photos = photoList.size();
        nextButton.setVisibility((photoIndex < num_photos - 1) ? View.VISIBLE : View.GONE);
        prevButton.setVisibility((photoIndex > 0) ? View.VISIBLE : View.GONE);
    }

    private void showTags() {
        Set<String> persons = p.getTags().get("person");
        Set<String> locations = p.getTags().get("location");

        String personText = persons.isEmpty()
                ? "none"
                : TextUtils.join(", ", persons);

        String locText = locations.isEmpty()
                ? "none"
                : TextUtils.join(", ", locations);

        tagList.setText("person: " + personText + "\n" +
                "location: " + locText);
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
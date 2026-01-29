package com.example.photosandroid.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.photosandroid.R;
import com.example.photosandroid.adapter.AlbumAdapter;
import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Model;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button addAlbumBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Model instance = Model.getInstance();
        ArrayList<String> albumNames = instance.getAlbumsByName();

        listView = findViewById(R.id.album_list);
        addAlbumBtn = findViewById(R.id.add_album);
        AlbumAdapter adapter =
                new AlbumAdapter(this, Model.getInstance().albums);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                openAlbum((String) parent.getItemAtPosition(position), position);
            }
        });

        findViewById(R.id.search_button_home).setOnClickListener(v -> {
            Intent intent =  new Intent(this, SearchPhotos.class);
            startActivity(intent);
        });

        addAlbumBtn.setOnClickListener(v -> showAddAlbumDialog());
    }

    private void renameAlbum(int index, String newName) {
        newName = newName.trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        for (int i = 0; i < Model.getInstance().albums.size(); i++) {
            if (i == index) continue;   // skip itself
            Album a = Model.getInstance().albums.get(i);
            if (a.getName().equalsIgnoreCase(newName)) {
                Toast.makeText(this, "Album name already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Album album = Model.getInstance().albums.get(index);
        album.setName(newName);
        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        Model.getInstance().save(this);
    }
    private void showAddAlbumDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Album");

        final EditText input = new EditText(this);
        input.setHint("Album name");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            addAlbum(name);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void addAlbum(String name) {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }


        for (Album a : Model.getInstance().albums) {
            if (a.getName().equalsIgnoreCase(name)) {
                Toast.makeText(this, "Album already exists", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Album newAlbum = new Album(name);
        Model.getInstance().albums.add(newAlbum);

        ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
        Model.getInstance().save(this);
    }
    public void showRenameDialog(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename Album");

        final EditText input = new EditText(this);
        input.setHint("New name");
        builder.setView(input);

        builder.setPositiveButton("OK", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            renameAlbum(index, newName);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    public void showDeleteDialog(int index) {
        Album album = Model.getInstance().albums.get(index);

        new AlertDialog.Builder(this)
                .setTitle("Delete Album")
                .setMessage("Delete '" + album.getName() + "'?")
                .setPositiveButton("Delete", (d,i) -> {
                    Model.getInstance().albums.remove(index);
                    ((BaseAdapter)listView.getAdapter()).notifyDataSetChanged();
                    Model.getInstance().save(this);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    public void openAlbum(String name, int pos) {
        Bundle bundle = new Bundle();
        bundle.putString("album_name",name);
        bundle.putInt("album_position",pos);
        Intent intent = new Intent(this, ShowAlbum.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
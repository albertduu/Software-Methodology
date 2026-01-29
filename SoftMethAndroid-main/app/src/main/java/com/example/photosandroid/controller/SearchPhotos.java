package com.example.photosandroid.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
import com.example.photosandroid.search.SearchEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchPhotos extends AppCompatActivity {
    private Spinner type1Spinner, type2Spinner;
    private AutoCompleteTextView value1Edit, value2Edit;
    private RadioGroup logicGroup;
    private Button searchButton;
    private GridView resultsGrid;

    private SearchEngine engine;
    private List<String> personValues = new ArrayList<>();
    private List<String> locationValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar myToolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(myToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        gatherTagValues();

        engine = new SearchEngine();

        type1Spinner = findViewById(R.id.spinner_type1);
        type2Spinner = findViewById(R.id.spinner_type2);

        value1Edit = findViewById(R.id.edit_value1);
        value2Edit = findViewById(R.id.edit_value2);

        logicGroup = findViewById(R.id.logic_group);
        searchButton = findViewById(R.id.search_button);

        resultsGrid = findViewById(R.id.results_grid);

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> adapter2 =
                new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);

        value1Edit.setAdapter(adapter1);
        value2Edit.setAdapter(adapter2);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        new String[]{"person", "location"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type1Spinner.setAdapter(adapter);
        type2Spinner.setAdapter(adapter);

        value1Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(type1Spinner.getSelectedItem().toString(), s.toString(), adapter1);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        value2Edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSuggestions(type2Spinner.getSelectedItem().toString(), s.toString(), adapter2);
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        searchButton.setOnClickListener(v -> performSearch());
    }

    private void updateSuggestions(String type, String prefix, ArrayAdapter<String> adapter) {
        adapter.clear();

        if (prefix.isEmpty())
            return;

        List<String> src = type.equals("person") ? personValues : locationValues;

        prefix = prefix.toLowerCase();

        for (String s : src) {
            if (s.toLowerCase().startsWith(prefix)) {
                adapter.add(s);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void performSearch() {
        String type1 = type1Spinner.getSelectedItem().toString();
        String type2 = type2Spinner.getSelectedItem().toString();

        String val1 = value1Edit.getText().toString();
        String val2 = value2Edit.getText().toString();

        boolean useAnd = (logicGroup.getCheckedRadioButtonId() == R.id.radio_and);

        ArrayList<Photo> results =
                engine.search(type1, val1, type2, val2, useAnd);

        resultsGrid.setAdapter(new PhotoGridAdapter(this, results));


        resultsGrid.setOnItemClickListener((p, view, pos, id) -> {
            Bundle bundle = new Bundle();
            bundle.putString("mode", "search");
            bundle.putInt("start_index", pos);
            Intent intent = new Intent(this, ShowPhoto.class);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }
    private void gatherTagValues() {
        Model m = Model.getInstance();

        for (Album a : m.albums) {
            for (Photo p : a.getPhotos()) {
                for (String val : p.getTags().get("person")) {
                    addUniqueLower(personValues, val);
                }
                for (String val : p.getTags().get("location")) {
                    addUniqueLower(locationValues, val);
                }
            }
        }
    }

    private void addUniqueLower(List<String> list, String val) {
        String lower = val.toLowerCase();
        for (String s : list) {
            if (s.toLowerCase().equals(lower))
                return;
        }
        list.add(val);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}

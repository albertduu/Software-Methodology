package com.example.photosandroid.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Photo implements Serializable {
    private transient Uri uri;
    private String uriString;
    private Map<String, HashSet<String>> tags = new LinkedHashMap<>();
    public Photo(Uri uri) {
        this.uri = uri;
        this.uriString = uri.toString();
        tags.put("person", new HashSet<String>());
        tags.put("location", new HashSet<String>());
    }

    public Uri getUri() {
        if(uri == null && uriString != null){
            uri = Uri.parse(uriString);
        }
        return uri;
    }
    public Map<String, HashSet<String>> getTags() { return tags; }
    public void addTag(String name, String value){
        String key = name.toLowerCase().trim();
        String val = value.trim();
        if(key.equals("location")){
            tags.get("location").clear();
        }
        tags.get(key).add(val);
    }

    public boolean hasTag(String name, String value) {
        if (tags == null || name == null || value == null) return false;
        name = name.toLowerCase();
        Set<String> values = tags.get(name);
        return values != null && values.contains(value);
    }

    public boolean removeTag(String name, String value) {
        String key = name.toLowerCase().trim();
        var set = tags.get(key);
        if (set == null) return false;
        return set.removeIf(v -> v.equalsIgnoreCase(value));
    }

    public boolean hasTagStartsWith(String tag, String value) {
        Set<String> tagValues = tags.get(tag);
        if (tagValues == null) return false;
        for (String val : tagValues) {
            if (val.toLowerCase().startsWith(value)) return true;
        }
        return false;
    }
}

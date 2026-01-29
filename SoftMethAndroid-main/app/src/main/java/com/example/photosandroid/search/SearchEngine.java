package com.example.photosandroid.search;

import com.example.photosandroid.model.Album;
import com.example.photosandroid.model.Model;
import com.example.photosandroid.model.Photo;

import java.util.ArrayList;

public class SearchEngine {

    /**
     * Search across all albums using up to two tag-value pairs.
     *
     * typeX should be "person" or "location".
     * valueX is a string (case-insensitive).
     *
     * If only one value is provided (value2 empty), it becomes a single-term search.
     * logic = true means AND
     * logic = false means OR
     */
    public static ArrayList<Photo> lastResults;
    public ArrayList<Photo> search(String type1, String value1,
                                   String type2, String value2,
                                   boolean useAnd) {

        ArrayList<Photo> results = new ArrayList<>();
        Model model = Model.getInstance();

        // Normalize inputs
        type1 = normalize(type1);
        type2 = normalize(type2);
        value1 = normalize(value1);
        value2 = normalize(value2);

        boolean has1 = !value1.isEmpty();
        boolean has2 = !value2.isEmpty();

        for (Album album : model.albums) {
            for (Photo p : album.getPhotos()) {

                boolean m1 = false;
                boolean m2 = false;

                if (has1) m1 = matchPair(p, type1, value1);
                if (has2) m2 = matchPair(p, type2, value2);

                boolean ok;
                if (has1 && has2) {
                    ok = useAnd ? (m1 && m2) : (m1 || m2);
                } else if (has1) {
                    ok = m1;
                } else if (has2) {
                    ok = m2;
                } else {
                    ok = false;
                }

                if (ok) results.add(p);
            }
        }
        lastResults = results;
        return results;
    }

    private boolean matchPair(Photo p, String type, String value) {
        if (type.equals("person") || type.equals("location"))  return p.hasTagStartsWith(type,value);
        return false;
    }


    private String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase();
    }
}

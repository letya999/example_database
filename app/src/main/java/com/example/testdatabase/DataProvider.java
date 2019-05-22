package com.example.testdatabase;

import android.content.SearchRecentSuggestionsProvider;

public class DataProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.polymap.DataProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public DataProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
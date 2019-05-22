package com.example.testdatabase;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private ListView searchlist;
    private ArrayAdapter<String> adapter;
    final String LOG_TAG = "test";
    public List <String> searchResult;
    public Search mSearch;
    public SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }
        try {
            mDb = mDBHelper.getWritableDatabase();
            mSearch = new Search(mDb);
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }
        searchView = (SearchView) findViewById(R.id.search);
        searchView.setSubmitButtonEnabled(true);
        int searchFrameId = searchView.getContext().getResources().getIdentifier("android:id/search_edit_frame", null, null);
        View searchFrame = searchView.findViewById(searchFrameId);
        searchFrame.setBackgroundResource(R.drawable.white_round);

        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = findViewById(searchPlateId);
        searchPlate.setBackgroundResource(R.drawable.white_round);

        int searchBarId = searchView.getContext().getResources().getIdentifier("android:id/search_bar", null, null);
        View searchBar = findViewById(searchBarId);
        searchBar.setBackgroundResource(R.drawable.white_round);
        searchlist = (ListView) findViewById(R.id.list_view);
        handleIntent(getIntent());

    }


    private void handleIntent(Intent intent) {
        Log.d(LOG_TAG, "Я в хендл интенте");
            Log.d(LOG_TAG, "Я в ифе");
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return onQueryTextChange(query);
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    try {
                        if (TextUtils.isEmpty(query)) {
                            searchlist.setVisibility(View.INVISIBLE);
                            return false;
                        }
                        searchResult = mSearch.search(query);
                        searchResult.addAll(mSearch.search(Search.changeCase(query)));
                        Log.d(LOG_TAG, searchResult.get(0));
                        adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, R.id.element, searchResult);
                        searchlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        searchlist.setAdapter(adapter);
                        searchlist.setVisibility(View.VISIBLE);
                        searchlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                searchView.setQuery(searchResult.get(position),true);
                            }
                        });
                    } catch (Exception ex) {
                    }
                    return true;
                }
            });
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean b) {
                searchlist.setVisibility(View.INVISIBLE);
            }
        });
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        mDb.close();
    }
}

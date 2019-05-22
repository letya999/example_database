package com.example.testdatabase;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Search {

    private SQLiteDatabase mDb;

    public Search(SQLiteDatabase mDb){
        this.mDb = mDb;
        mDb.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS fts_example_table USING fts4 (Name_build);");
        Log.d("test", "создали таблицу fts_example_table");
        mDb.execSQL("INSERT INTO fts_example_table SELECT Name_build FROM Building;");
        Log.d("test", "вставили в таблицу");
    }


    protected Cursor searchAnker(String input) throws SQLException {
        Log.d("test", "в searchAnker получили запрос "+input);
        String query = "SELECT Name_build FROM fts_example_table WHERE Name_build MATCH ?";
        String[] selectionArgs = new String[] { appendToken(input)};
        for(int i=0; i<selectionArgs.length; i++)
            Log.d("test", "в searchAnker получили результат "+selectionArgs[i]);
        Cursor mCursor = mDb.rawQuery(query,selectionArgs);
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return null;
        }
        else if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    protected String appendToken(String query) {
        if (TextUtils.isEmpty(query)) return query;

        final StringBuilder builder = new StringBuilder();
        final String[] splits = TextUtils.split(query, " ");

        for (String split : splits)
            builder.append(split).append("*").append(" ");
        Log.d("test", "в appendToken получили результат "+builder.toString().trim());
        return builder.toString().trim();
    }

    public List<String> search(String query) {
        Log.d("test", "в search получили запрос "+query);
        Cursor cursor = searchAnker(query);
        List <String> list = new ArrayList<String>();
        try {
            if (cursor.moveToFirst())
                do {
                    list.add(cursor.getString(0));
                } while (cursor.moveToNext());
            cursor.close();
        } catch (Exception ex) {
            list.clear();
        }
        for(int i=0; i<list.size(); i++)
            Log.d("test", "в search получили результат "+list.get(i));
        return list;
    }

    //поиск ведем по названию и адресу для зданий
    //по названию  и кабинету для организаций
    //возвращать будем LatLng или номер кабинета
    public <T> T choise(String select){
        String query = "SELECT Number_of_org FROM Building WHERE Name_build='"+select+"'";
        Cursor cursor = mDb.rawQuery(query, null);
        cursor.moveToFirst();
        int age = cursor.getInt(0);
        cursor.close();
        return (T) new Integer(age);
    }

    protected static String changeCase(String word){
        if(Character.isUpperCase(word.charAt(0)))
            word = word.substring(0, 1).toLowerCase() + word.substring(1);
        else
            word = word.substring(0, 1).toUpperCase() + word.substring(1);
        return word;
    }
}

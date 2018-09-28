package com.example.samy.coach_nutrition;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NutritionContentProvider extends ContentProvider {
    NutritionDatabase base;
    public final static String authority = "fr.ansarimetadjer.nutrition";
    public final static String TABLE_ALIMENTS = "aliments";
    public final static String TABLE_HISTORIQUE = "historique";
    public final static int ALIMENTS_ID = 1;
    public final static int HISTORIQUE_ID = 2;
    SQLiteDatabase bdd;

    public NutritionContentProvider() {
    }

    private final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    {
        matcher.addURI(authority, TABLE_ALIMENTS, ALIMENTS_ID);
        matcher.addURI(authority, TABLE_HISTORIQUE, HISTORIQUE_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        bdd = base.getWritableDatabase();
        int code = matcher.match(uri);
        long id = 0;
        String path;

        if (code == 1) {
            id = bdd.delete(TABLE_ALIMENTS, selection, selectionArgs);
            path = TABLE_ALIMENTS;
        } else if (code == 2) {
            id = bdd.delete(TABLE_HISTORIQUE, selection, selectionArgs);
            path = TABLE_HISTORIQUE;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        bdd = base.getWritableDatabase();
        int code = matcher.match(uri);
        long id = 0;
        String path;

        if (code == 1) {
            id = bdd.insert(TABLE_ALIMENTS, null, values);
            path = TABLE_ALIMENTS;
        } else if (code == 2) {
            id = bdd.insert(TABLE_HISTORIQUE, null, values);
            path = TABLE_HISTORIQUE;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }

        Uri.Builder builder = (new Uri.Builder())
                .authority(authority)
                .appendPath(path);

        return ContentUris.appendId(builder, id).build();
    }

    @Override
    public boolean onCreate() {
        base = NutritionDatabase.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int code = matcher.match(uri);
        long id = 0;
        String path;
        if (code == 1) {
            Cursor cursor = base.getReadableDatabase().query(TABLE_ALIMENTS, projection, selection, selectionArgs, null, null, null);
            path = TABLE_ALIMENTS;
            return cursor;
        } else if (code == 2) {
            Cursor cursor = base.getReadableDatabase().query(TABLE_HISTORIQUE, projection, selection, selectionArgs, null, null, null);
            path = TABLE_HISTORIQUE;
            return cursor;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int code = matcher.match(uri);
        long id = 0;
        String path;
        if (code == 1) {
            int cursor = base.getWritableDatabase().update(TABLE_ALIMENTS, values, selection, selectionArgs);
            path = TABLE_ALIMENTS;
            return cursor;
        } else if (code == 2) {
            int cursor = base.getWritableDatabase().update(TABLE_HISTORIQUE, values, selection, selectionArgs);
            path = TABLE_HISTORIQUE;
            return cursor;
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }
}

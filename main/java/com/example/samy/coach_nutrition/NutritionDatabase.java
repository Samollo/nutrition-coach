package com.example.samy.coach_nutrition;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NutritionDatabase extends SQLiteOpenHelper {

    public final static int VERSION = 1;
    public final static String DB_NAME = "nutrition_base";

    public final static String TABLE_ALIMENTS = "aliments";
    public final static String COLONNE_ID = "id";
    public final static String COLONNE_NOM = "nom";
    public final static String COLONNE_CALORIES = "calories";

    public final static String TABLE_HISTORIQUE = "historique";
    public final static String COLONNE_DATE = "date";
    public final static String COLONNE_APPORT = "apport";
    public final static String COLONNE_DETAILS = "details";


    public final static String CREATE_ALIMENTS = "create table " + TABLE_ALIMENTS + "(" +
            COLONNE_ID + " integer not null primary key, " +
            COLONNE_NOM + " string, " +
            COLONNE_CALORIES + " float " + ");";

    public final static String CREATE_HISTORIQUE = "create table " + TABLE_HISTORIQUE + "(" +
            COLONNE_DATE + " string primary key, " +
            COLONNE_APPORT + " float, " +
            COLONNE_DETAILS + " string " + ");";

    private static NutritionDatabase ourInstance;

    public static NutritionDatabase getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new NutritionDatabase(context);
        return ourInstance;
    }

    private NutritionDatabase(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALIMENTS);
        db.execSQL(CREATE_HISTORIQUE);
/*
        ContentValues row = new ContentValues();
        row.put(NutritionDatabase.COLONNE_NOM, "Baguette de pain");
        row.put(NutritionDatabase.COLONNE_CALORIES, "700");
        db.insert(NutritionDatabase.TABLE_ALIMENTS, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_NOM, "Riz");
        row.put(NutritionDatabase.COLONNE_CALORIES, "80.5");
        db.insert(NutritionDatabase.TABLE_ALIMENTS, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_NOM, "Viande boeuf (steak 160g)");
        row.put(NutritionDatabase.COLONNE_CALORIES, "400");
        db.insert(NutritionDatabase.TABLE_ALIMENTS, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_NOM, "Aile de poulet (30g)");
        row.put(NutritionDatabase.COLONNE_CALORIES, "78");
        db.insert(NutritionDatabase.TABLE_ALIMENTS, null, row);
        row.clear();


        row.put(NutritionDatabase.COLONNE_DATE, "01/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "100");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "02/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "200");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "03/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "300");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "04/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "400");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "05/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "500");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "06/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "600");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();
        row.put(NutritionDatabase.COLONNE_DATE, "07/11/2017");
        row.put(NutritionDatabase.COLONNE_APPORT, "700");
        row.put(NutritionDatabase.COLONNE_DETAILS, "");
        db.insert(NutritionDatabase.TABLE_HISTORIQUE, null, row);
        row.clear();*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists " + TABLE_ALIMENTS);
            db.execSQL("drop table if exists " + TABLE_HISTORIQUE);
            onCreate(db);
        }
    }
}

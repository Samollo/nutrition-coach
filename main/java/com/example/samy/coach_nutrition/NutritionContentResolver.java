package com.example.samy.coach_nutrition;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NutritionContentResolver {
    public final static String TABLE_ALIMENTS = "aliments";
    public final static String COLONNE_ID = "id";
    public final static String COLONNE_NOM = "nom";
    public final static String COLONNE_CALORIES = "calories";

    public final static String TABLE_HISTORIQUE = "historique";
    public final static String COLONNE_DATE = "date";
    public final static String COLONNE_APPORT = "apport";
    public final static String COLONNE_DETAILS = "details";

    public final static String authority = "fr.ansarimetadjer.nutrition";

    public final static int ALIMENTS_ID = 1;
    public final static int HISTORIQUE_ID = 2;

    private Context context;
    private ContentResolver cr;

    int TABLE_ID;

    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

    public NutritionContentResolver(Context context, int id) {
        this.context = context;
        this.cr = this.context.getContentResolver();

        this.TABLE_ID = id;
    }

    public int ajouter (float apport) throws ParseException {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            ContentValues values = new ContentValues();

            Date date = new Date();
            int day = date.getDate();
            int month = date.getMonth() + 1;
            int year = date.getYear() + 1900;
            String today;

            if (day < 10) {
                today = "0";
                today = today + Integer.toString(day) + "/";
            } else
                today = Integer.toString(day) + "/";
            if (month < 10)
                today = today + "0";
            today = today + Integer.toString(month) + "/" + Integer.toString(year);

            Cursor request = find(today);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            if (request == null) {
                values.put(COLONNE_DATE, today);
                values.put(COLONNE_APPORT, apport);
                values.put(COLONNE_DETAILS, "");

                String substitute = toReplace();
                if (substitute == null) {
                    if (cr.insert(uri, values) == null)
                        return -1;
                    else
                        return 1;
                } else {
                    Log.d("REPLACE", substitute);
                    cr.delete(uri, COLONNE_DATE + " = ?", new String[]{substitute});

                    if (cr.insert(uri, values) == null)
                        return -1;
                    else
                        return 1;
                }
            } else {
                String value = request.getString(1);
                apport = Float.parseFloat(value) + apport;
                value = Float.toString(apport);
                values.put(COLONNE_APPORT, value);

                return cr.update(uri, values, COLONNE_DATE + " = ?", new String[]{today});
            }
        } else
            return -1;
    }

    public int ajouter (String details) throws ParseException {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            ContentValues values = new ContentValues();

            Date date = new Date();
            int day = date.getDate();
            int month = date.getMonth() + 1;
            int year = date.getYear() + 1900;
            String today;

            if (day < 10) {
                today = "0";
                today = today + Integer.toString(day) + "/";
            } else
                today = Integer.toString(day) + "/";
            if (month < 10)
                today = today + "0";
            today = today + Integer.toString(month) + "/" + Integer.toString(year);

            Cursor request = find(today);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            Log.d("herrrrrrrrrrrrrrrrrre", today);
            Log.d("herrrrrrrrrrrrrrrre", "test "+ details);

            values.put(COLONNE_DETAILS, details);
            if (today != null) {
                cr.update(uri, values, COLONNE_DATE + " = ?", new String[]{today});
                return 1;
            }
            else
                return -1;
        } else
            return -1;
    }

    public int ajouter (String nom, String calories) {
        if (this.TABLE_ID == ALIMENTS_ID) {
            ContentValues values = new ContentValues();
            values.put(COLONNE_NOM, nom);
            values.put(COLONNE_CALORIES, calories);

            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            Uri uri = builder.build();

            if (retourner(nom) == null) {
                uri = cr.insert(uri, values);
                return 1;
            } else
                return -1;
        } else
            return -1;
    }

    public String[][] getAllElements() {
        Uri.Builder builder = new Uri.Builder();
        if (this.TABLE_ID == HISTORIQUE_ID) {
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            String[] columns = new String[]{"*"};
            Cursor ch = cr.query(uri, columns, null, null, null);
            ch.moveToFirst();

            if (ch.getCount() == 0)
                return null;
            else {
                String[][] values = new String[ch.getCount()][ch.getColumnCount()];
                int i = 0;
                do {
                    values[i] = new String[]{ch.getString(0), ch.getString(1), ch.getString(2)};
                    i++;
                } while (ch.moveToNext());
                return values;
            }
        } else if (this.TABLE_ID == ALIMENTS_ID) {
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            String[] columns = new String[]{"*"};
            Uri uri = builder.build();
            Cursor ch = cr.query(uri, columns,null,null,null);
            ch.moveToFirst();

            if (ch.getCount() == 0)
                return null;
            else {
                String[][] values = new String[ch.getCount()][ch.getColumnCount()];
                int i = 0;
                do {
                    values[i] = new String[]{ch.getString(0), ch.getString(1), ch.getString(2)};
                    i++;
                } while (ch.moveToNext());
                return values;
            }
        } else
            return null;
    }

    public Cursor find(String element) {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            String[] columns = new String[]{COLONNE_DATE, COLONNE_APPORT};
            Cursor ch = cr.query(uri, columns, COLONNE_DATE + "==" + "'" + element + "'", null, null);
            ch.moveToFirst();
            if (ch.getCount() == 0)
                return null;
            else
                return ch;
        } else if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            Uri uri = builder.build();

            String[] columns = new String[]{COLONNE_ID, COLONNE_NOM, COLONNE_CALORIES};
            Cursor ch = cr.query(uri, columns, COLONNE_NOM + "==" + "'" + element + "'", null, null);
            ch.moveToFirst();
            if (ch.getCount() == 0)
                return null;
            else
                return ch;
        } else
            return null;
    }

    public int getTableSize() {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            String[] columns = new String[]{"*"};
            Cursor ch = cr.query(uri, columns, null, null, null);
            ch.moveToFirst();

            return ch.getCount();
        } else if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            Uri uri = builder.build();

            String[] columns = new String[]{"*"};
            Cursor ch = cr.query(uri, columns, null, null, null);
            ch.moveToFirst();

            return ch.getCount();
        } else
            return -1;
    }

    public String[] getAllDate() {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_HISTORIQUE);
            Uri uri = builder.build();

            String[] columns = new String[]{COLONNE_DATE};
            Cursor ch = cr.query(uri, columns, null, null, null);
            ch.moveToFirst();
            String[] values = new String[ch.getCount()];

            for (int i = 0; i < ch.getCount(); i++) {
                values[i] = ch.getString(0);
                ch.moveToNext();
            }
            return values;
        }
        else
            return null;
    }

    public String toReplace() throws ParseException {
        if (this.TABLE_ID == HISTORIQUE_ID) {
            if (getTableSize() == 8) {
                String[] allDate = getAllDate();
                if (allDate.length != 0) {
                    String value = allDate[0];
                    Date date1 = formatter.parse(value);
                    for (int i = 1; i < allDate.length; i++) {
                        Date date2 = formatter.parse(allDate[i]);
                        if (date1.compareTo(date2) > 0) {
                            value = allDate[i];
                            date1 = formatter.parse(value);
                        }
                    }
                    return value;
                } else return null;
            } else return null;
        }
        else
            return null;
    }

    public String[] getNameElements () {
        if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            String[] columns = new String[]{COLONNE_NOM};
            Uri uri = builder.build();
            Cursor ch = cr.query(uri, columns, null, null, null);
            ch.moveToFirst();
            if (ch.getCount() == 0)
                return null;
            else {
                String[] values = new String[ch.getCount()];
                int i = 0;
                do {
                    values[i] = ch.getString(0);
                    i++;
                } while (ch.moveToNext());
                return values;
            }
        } else
            return null;
    }

    public String[] retourner (String nom) {
        if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            String[] columns = new String[]{COLONNE_ID, COLONNE_NOM, COLONNE_CALORIES};
            Uri uri = builder.build();
            Cursor ch = cr.query(uri, columns, COLONNE_NOM + "==" + "'" + nom + "'", null, null);
            ch.moveToFirst();
            if (ch.getCount() == 0)
                return null;
            else
                return new String[]{ch.getString(0), ch.getString(1), ch.getString(2)};
        } else
            return null;
    }

    public String getCalorie(String nom) {
        if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            String[] columns = new String[]{COLONNE_CALORIES};
            Uri uri = builder.build();
            Cursor ch = cr.query(uri, columns, COLONNE_NOM + "=='" + nom + "'", null, null);
            ch.moveToFirst();
            if (ch.getCount() == 0)
                return null;
            else
                return ch.getString(0);
        } else
            return null;
    }

    public void removeItem(String nom) {
        if (this.TABLE_ID == ALIMENTS_ID) {
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath(TABLE_ALIMENTS);
            Uri uri = builder.build();

            cr.delete(uri, COLONNE_NOM + " = ?", new String[]{nom});
        }
    }
}

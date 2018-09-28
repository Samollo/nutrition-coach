package com.example.samy.coach_nutrition;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.hardware.camera2.params.RggbChannelVector.RED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String aliment, listFromData;
    float quantite, apports, dayConsume;
    float minGoal, maxGoal;

    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList = new ArrayList<String>();

    NutritionContentResolver cr;

    ListView repasListView;
    TextView textViewMin, textViewMax, textViewConso;
    Toolbar toolbar;
    View ecran;

    Notification.Builder notificationBuilder;

    SharedPreferences pref, style;

    @TargetApi(16)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("YOOOOO");

        ecran = findViewById(R.id.ecran);
        repasListView = findViewById(R.id.listView);
        textViewMin = findViewById(R.id.textViewMin);
        textViewMax = findViewById(R.id.textViewMax);
        textViewConso = findViewById(R.id.textViewConso);
        toolbar = findViewById(R.id.toolbar);

        pref = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        style = getSharedPreferences("STYLE", Context.MODE_PRIVATE);

        if(pref.getBoolean("firstStart", true)){
            SharedPreferences.Editor prefEdit = pref.edit();
            prefEdit.putBoolean("firstStart", false);
            prefEdit.apply();
            loadCSV();
        }

        dayConsume = pref.getFloat("dayConsume", 0);

        notificationBuilder = new Notification.Builder(this)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(getResources().getString(R.string.notificationValue))
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_done_black_18dp)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0));
        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notificationBuilder.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1*1000*60*60*4, 1*1000*60*60*4, pendingIntent);

        setSupportActionBar(toolbar);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        repasListView.setAdapter(adapter);

        repasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                        .setTitle("Retirer aliment")
                        .setIcon(R.drawable.ic_warning_black_18dp)
                        .setMessage("Souhaitez-vous retirer l'aliment de votre liste pour ce jour ?")
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String foodList = pref.getString("foodList", "");
                                if (!foodList.isEmpty()) {
                                    String[] tmp = foodList.split(";");
                                    String[] bis = tmp[position].split("/");
                                    String listUpToDate = "";
                                    for(int i = 0 ; i < tmp.length; i++){
                                        if(i != position) {
                                            Log.d("TAG/REMOVE", "i : " + i);
                                            Log.d("TAG/REMOVE", "tmp[" + i + "] : " + tmp[i]);
                                            listUpToDate += tmp[i] + ";";
                                        }
                                    }
                                    Log.d("TAG/REMOVE", "la cible tmp["+position+"] : " + tmp[position]);
                                    Log.d("TAG/REMOVE", "value dans updateMeal vaudra : " + (-Float.parseFloat(bis[2])) );
                                    updateMeal(-Float.parseFloat(bis[2]), listUpToDate, "", 0);
                                    arrayList.remove(position);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        })
                        .setNegativeButton(R.string.no, null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        final Intent AjoutAlimentsRepasIntent = new Intent(this, AjoutAlimentsRepasActivity.class);

        cr = new NutritionContentResolver(this, NutritionContentResolver.HISTORIQUE_ID);
        try {
            cr.ajouter(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(AjoutAlimentsRepasIntent, 1);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        listFromData = pref.getString("foodList", "");
        if (!listFromData.isEmpty()) {
            String[] tmp = listFromData.split(";");
            Log.d("TAG/tmpLENGTH", "onCreate: " + tmp.length);
            for (int i = 0; i < tmp.length; i++) {
                String[] tmpBis = tmp[i].split("/");
                Log.d("TAG/READSHARED", "onCreate: tmpBis[0]" + tmpBis[0]);
                arrayList.add(tmpBis[0] + " | Quantité : " + tmpBis[1] + " \n Apports : " + tmpBis[2] + " calories");
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String usernameDef = "Bruce Wayne";
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences.Editor prefEdit = pref.edit();
        Date date = new Date();
        int day = date.getDate();
        int month = date.getMonth() + 1;
        int year = date.getYear() + 1900;
        String today = Integer.toString(day) + Integer.toString(month) + Integer.toString(year);
        if(!today.equals(pref.getString("today", ""))){
            prefEdit.putString("today", today);
            prefEdit.putString("foodList", "");
            prefEdit.putFloat("dayConsume", 0);
            prefEdit.apply();
            arrayList.clear();
            adapter.notifyDataSetChanged();
            updateMeal(0, "", null, 0);
        }

        minGoal = pref.getFloat("minGoal", 2150);
        maxGoal = pref.getFloat("maxGoal", 2735);
        textViewMin.setText(getResources().getString(R.string.minimumConso) + Float.toString(minGoal) + " cal.");
        textViewMax.setText(getResources().getString(R.string.maximumConso) + Float.toString(maxGoal) + " cal.");
        textViewConso.setText(getResources().getString(R.string.todayConso) + Float.toString(dayConsume) + " cal.");

        int colorPreferences = style.getInt("background", 3);
        if (colorPreferences == 1)
            ecran.setBackgroundResource(R.drawable.blanc);
        else if (colorPreferences == 2)
            ecran.setBackgroundResource(R.drawable.coachnutrition);
        else if (colorPreferences == 3)
            ecran.setBackgroundResource(R.drawable.d5283f0);

    }

    public void updateMeal(float value, String listUptodate, String almt, float qte){
        SharedPreferences.Editor prefEdit = pref.edit();
        Log.d("updateMeal", "listUptodate : " + listUptodate);

        if(qte == 0){
            prefEdit.putString("foodList", listUptodate);
            try {
                cr.ajouter(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        else if(qte != 0){
            String foodList = pref.getString("foodList", "");
            prefEdit.putString("foodList", foodList + almt + "/" + qte + "/" + value + ";");
            Log.d("updateMeal", "else if: I'm in. ");
        }

        Log.d("updateMeal", "value of dayConsume : " + dayConsume);
        Log.d("updateMeal", "value of value : " + value);
        dayConsume = pref.getFloat("dayConsume", 0) + value;
        Log.d("updateMeal", "value of dayConsume : " + dayConsume);
        prefEdit.putFloat("dayConsume", dayConsume);
        prefEdit.apply();
        try {
            cr.ajouter(pref.getString("foodList", "not found"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (maxGoal + 250 < dayConsume)
            textViewConso.setBackgroundColor(getResources().getColor(R.color.maxSurpassed));
        else
            textViewConso.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        Log.d("updateMeal", "foodList : " + pref.getString("foodList", "not found"));
        textViewConso.setText(getResources().getString(R.string.todayConso) + Float.toString(dayConsume) + " cal.");
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) { // Please, use a final int instead of hardcoded int value
            if (resultCode == RESULT_OK) {
                aliment = data.getStringExtra("aliment");
                quantite = data.getFloatExtra("quantite", 0);
                apports = data.getFloatExtra("apports", 0);
                arrayList.add(aliment + " | Quantité : " + Float.toString(quantite) + " \n Apports :" + Float.toString(quantite * apports) + " calories");
                adapter.notifyDataSetChanged();

                updateMeal(quantite*apports, "", aliment, quantite);
                Log.d("TAG", "onActivityResult: updateMeal executé");
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {

        } else if (id == R.id.nav_foodList) {
            final Intent AlimentsListIntent = new Intent(this, AlimentsListActivity.class);
            startActivity(AlimentsListIntent);
        } else if (id == R.id.nav_settings) {
            final Intent SettingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(SettingsIntent);
        } else if (id == R.id.nav_history) {
            final Intent HistoriqueIntent = new Intent(this, HistoriqueActivity.class);
            startActivity(HistoriqueIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void loadCSV(){
        InputStream is = getResources().openRawResource(R.raw.database);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try {
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(";");
                NutritionContentResolver cra = new NutritionContentResolver(this, NutritionContentResolver.ALIMENTS_ID);
                cra.ajouter(tokens[0], tokens[1]);
            }
        } catch (IOException e1) {
            Log.e("MainActivity", "Error" + line, e1);
            e1.printStackTrace();
        }
    }

    public Context getActivity() {
        return this;
    }
}
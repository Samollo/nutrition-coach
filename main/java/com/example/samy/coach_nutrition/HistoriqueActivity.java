package com.example.samy.coach_nutrition;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoriqueActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public NutritionContentResolver cr;
    View ecranHistorique;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        ecranHistorique = (View) findViewById(R.id.ecranHistorique);
        pref = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        float min = pref.getFloat("minGoal", 2150);
        float max = pref.getFloat("minGoal", 2735);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        cr = new NutritionContentResolver(this, NutritionContentResolver.HISTORIQUE_ID);

        ListView simpleList = (ListView) findViewById(R.id.HistoriqueListView);
        ArrayList<Item> historiqueList=new ArrayList<>();

        final String[][] listHistorique = cr.getAllElements();
        int borne;
        if (listHistorique.length < 8)
            borne = listHistorique.length;
        else
            borne = 8;
        if (listHistorique != null) {
            for (int i = 0; i < borne; i++) {
                String start;
                if (i == borne-1)
                    start = "Aujourd'hui : ";
                else
                    start = "Jour " + Integer.toString(i) + " : ";


                if (Float.parseFloat(listHistorique[i][1]) < min)
                    historiqueList.add(new Item(start + listHistorique[i][1], R.drawable.button_blue));
                else if (Float.parseFloat(listHistorique[i][1]) > max)
                    historiqueList.add(new Item(start + listHistorique[i][1], R.drawable.button_red));
                else
                    historiqueList.add(new Item(start + listHistorique[i][1], R.drawable.button_green));
            }
        }
        MyAdapter myAdapter=new MyAdapter(this, R.layout.list_view_items, historiqueList);
        simpleList.setAdapter(myAdapter);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Jour " + (position+1));
                builder.setMessage("Date : " + listHistorique[position][0] + "\n" +
                        "Consommation : " + listHistorique[position][1] + " calorie(s)");
                builder.setPositiveButton("Retour", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        simpleList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                final Dialog dialog = new Dialog(HistoriqueActivity.this);
                dialog.setContentView(R.layout.history_dialog);
                ListView list= (ListView) dialog.findViewById(R.id.mealView);
                ArrayList<String> arrayList=new ArrayList<>();
                ArrayAdapter<String> adapter = new ArrayAdapter(HistoriqueActivity.this,android.R.layout.simple_list_item_1, arrayList);
                arrayList.add(listHistorique[position][0]);
                adapter.notifyDataSetChanged();
                Log.d("TAG", "onItemLongClick: " + listHistorique[position][0]);
                if(listHistorique[position][2].length() > 0 ) {
                    Log.d("TAG", "onItemLongClick: im in");
                    String[] tmp = listHistorique[position][2].split(";");
                    for (int i = 0; i < tmp.length; i++) {
                        String[] bis = tmp[i].split("/");
                        arrayList.add(bis[1] + " " + bis[0] + " (" + bis[2] + " cal.) ");
                    }
                    adapter.notifyDataSetChanged();
                }
                list.setAdapter(adapter);
                dialog.show();

                return true;
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences pref = getSharedPreferences("STYLE", Context.MODE_PRIVATE);
        int colorPreferences = pref.getInt("background",3);

        if (colorPreferences == 1)
            ecranHistorique.setBackgroundResource(R.drawable.blanc);
        else if (colorPreferences == 2)
            ecranHistorique.setBackgroundResource(R.drawable.coachnutrition);
        else if (colorPreferences == 3)
            ecranHistorique.setBackgroundResource(R.drawable.d5283f0);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.historique, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, GraphiqueActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_today) {
            finish();
        }
        else if (id == R.id.nav_foodList) {
            final Intent AlimentsListIntent = new Intent(this, AlimentsListActivity.class);
            finish();
            startActivity(AlimentsListIntent);
        } else if (id == R.id.nav_settings) {
            final Intent SettingsIntent = new Intent(this, SettingsActivity.class);
            finish();
            startActivity(SettingsIntent);
        } else if (id == R.id.nav_history) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Context getActivity() {
        return this;
    }
}

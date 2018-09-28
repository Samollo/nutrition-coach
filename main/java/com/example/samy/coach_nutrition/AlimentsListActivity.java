package com.example.samy.coach_nutrition;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class AlimentsListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public NutritionContentResolver cr;

    View ecranAliment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aliments_list);
        cr = new NutritionContentResolver(this, NutritionContentResolver.ALIMENTS_ID);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, 0, 0);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ecranAliment = (View) findViewById(R.id.ecranAliment);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences pref = getSharedPreferences("STYLE", Context.MODE_PRIVATE);
        int colorPreferences = pref.getInt("background", 3);

        if (colorPreferences == 1)
            ecranAliment.setBackgroundResource(R.drawable.blanc);
        else if (colorPreferences == 2)
            ecranAliment.setBackgroundResource(R.drawable.coachnutrition);
        else if (colorPreferences == 3)
            ecranAliment.setBackgroundResource(R.drawable.d5283f0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ListView AlimentsListView = findViewById(R.id.AlimentsListView);
        ArrayList<String> toPrint = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, toPrint);

        String[] listAliments = cr.getNameElements();
        if (listAliments != null) {
            for (int i = 0; i < listAliments.length; i++) {
                toPrint.add(listAliments[i]);
            }
        } else
            adapter.clear();
        AlimentsListView.setAdapter(adapter);
        AlimentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <? > arg0, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String selectedItem = (String) arg0.getItemAtPosition(position);
                builder.setTitle(selectedItem);
                builder.setMessage("Apport : "+cr.getCalorie(selectedItem)+" calorie(s)");
                builder.setPositiveButton("Retour", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        AlimentsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final String selectedItem = (String) arg0.getItemAtPosition(position);
                builder.setTitle("Retirer aliment");
                builder.setIcon(R.drawable.ic_warning_black_18dp);
                builder.setMessage("Souhaitez-vous retirer l'aliment de votre liste d'aliments ?");
                builder.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cr.removeItem(selectedItem);
                    }
                });
                builder.setNegativeButton("Non", null);
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });

        FloatingActionButton fab = (findViewById(R.id.fab));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                LinearLayout layout = new LinearLayout(builder.getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final EditText AlimentInput = new EditText(builder.getContext());
                AlimentInput.setHint("Aliment");
                layout.addView(AlimentInput);

                final EditText ApportInput = new EditText(builder.getContext());
                ApportInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
                ApportInput.setHint("Apport par portion");
                layout.addView(ApportInput);

                builder.setView(layout);
                builder.setCancelable(true);
                builder.setTitle("Ajouter un élément");

                builder.setPositiveButton("Ajouter", null);
                builder.setNegativeButton("Annuler", null);

                AlertDialog dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button b = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                String aliment = AlimentInput.getText().toString();
                                String apport = ApportInput.getText().toString();
                                int isOk = 0;

                                if ((aliment.replace(" ", "")).isEmpty())
                                    AlimentInput.setError("Ne peut être vide");
                                else
                                    isOk = isOk + 1;
                                if ((apport.replace(" ", "")).isEmpty())
                                    ApportInput.setError("Ne peut être vide");
                                else
                                    isOk = isOk + 2;

                                if (isOk == 3) {
                                    if (cr.ajouter(AlimentInput.getText().toString(), ApportInput.getText().toString()) == -1) {
                                        Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                        vib.vibrate(40);
                                        Toast mess = new Toast(getActivity()).makeText(builder.getContext(), "Aliment déjà existant", Toast.LENGTH_LONG);
                                        mess.show();
                                    } else {
                                        adapter.add(AlimentInput.getText().toString());
                                        adapter.notifyDataSetChanged();

                                        Toast mess = new Toast(getActivity()).makeText(builder.getContext(), "Aliment ajouté", Toast.LENGTH_LONG);
                                        mess.show();
                                        dialog.dismiss();
                                    }
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }

        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
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
            final Intent HistoriqueIntent = new Intent(this, HistoriqueActivity.class);
            finish();
            startActivity(HistoriqueIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public Context getActivity() {
        return this;
    }

}

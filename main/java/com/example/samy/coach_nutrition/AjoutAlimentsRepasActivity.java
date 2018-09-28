package com.example.samy.coach_nutrition;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;

public class AjoutAlimentsRepasActivity extends AppCompatActivity {

    Button bajouter;
    Spinner spinner;
    EditText quantite;
    NutritionContentResolver nacr;
    NutritionContentResolver nhcr;
    String[] table;
    ArrayList<String> toPrint;
    View ecranAjout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajout_aliments_repas);

        bajouter = findViewById(R.id.bajouter);
        ecranAjout = findViewById(R.id.ecranAjout);
        quantite = findViewById(R.id.quantite);
        spinner = findViewById(R.id.spinner);

        nacr = new NutritionContentResolver(this, NutritionContentResolver.ALIMENTS_ID);
        nhcr = new NutritionContentResolver(this, NutritionContentResolver.HISTORIQUE_ID);

        toPrint = new ArrayList<>();
    }

    @Override
    protected void onStart(){
        super.onStart();

        SharedPreferences pref = getSharedPreferences("STYLE", Context.MODE_PRIVATE);
        int colorPreferences = pref.getInt("background", 3);

        if (colorPreferences == 1)
            ecranAjout.setBackgroundResource(R.drawable.blanc);
        else if (colorPreferences == 2) {
            ecranAjout.setBackgroundResource(R.drawable.coachnutrition);
        } else if (colorPreferences == 3)
            ecranAjout.setBackgroundResource(R.drawable.d5283f0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        table = nacr.getNameElements();
        toPrint = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            toPrint.add(table[i]);
        }
        toPrint.add("+");
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, toPrint);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == adapter.getCount() - 1) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    LinearLayout layout = new LinearLayout(builder.getContext());
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText AlimentInput = new EditText(builder.getContext());
                    AlimentInput.setInputType(InputType.TYPE_CLASS_TEXT);
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
                    builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            spinner.setSelection(0);
                        }
                    });

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
                                        if (nacr.ajouter(aliment, apport) == -1) {
                                            Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                            vib.vibrate(40);
                                            Toast mess = new Toast(getActivity()).makeText(builder.getContext(), "Aliment déjà existant", Toast.LENGTH_LONG);
                                            mess.show();
                                        } else {
                                            adapter.remove("+");
                                            adapter.add(AlimentInput.getText().toString());
                                            adapter.add("+");
                                            adapter.notifyDataSetChanged();

                                            Toast mess = new Toast(getActivity()).makeText(builder.getContext(), "Aliment ajouté", Toast.LENGTH_LONG);
                                            mess.show();
                                            dialog.dismiss();
                                        }
                                        spinner.setSelection(adapter.getCount() - 2);
                                    }
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        spinner.setAdapter(adapter);
    }

    public void ajouter(View button) throws ParseException {
        if (button == bajouter) {
            String qteToString = quantite.getText().toString();
            if (!qteToString.trim().isEmpty() && Float.parseFloat(qteToString) > 0) {
                float qte = Float.parseFloat(quantite.getText().toString().trim());
                String aliment = spinner.getSelectedItem().toString();
                Log.d("aliment", aliment);
                float calories = Float.parseFloat(nacr.getCalorie(aliment));
                nhcr.ajouter(calories * qte);
                Intent backTo = new Intent(this, MainActivity.class);
                backTo.putExtra("quantite", qte);
                backTo.putExtra("aliment", aliment);
                backTo.putExtra("apports", calories);
                setResult(RESULT_OK, backTo);
                finish();
            }
            else
                quantite.setError("- Non vide\n- Non nul");
        }
    }

    public Context getActivity() {
        return this;
    }
}

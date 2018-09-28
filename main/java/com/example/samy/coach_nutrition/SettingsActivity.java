package com.example.samy.coach_nutrition;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class SettingsActivity extends AppCompatActivity {

    Button validerSettings;
    EditText minGoal, maxGoal;
    SharedPreferences pref, style;
    ImageButton screen1, screen2, screen3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        screen1 = (ImageButton) findViewById(R.id.button_id1);
        screen2 = (ImageButton) findViewById(R.id.button_id2);
        screen3 = (ImageButton) findViewById(R.id.button_id3);

        pref = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        style = getSharedPreferences("STYLE", Context.MODE_PRIVATE);
        int colorPreferences = style.getInt("background", 3);
        if (colorPreferences == 1) {
            screen1.setImageResource(R.drawable.ic_done_black_18dp);
            screen2.setImageBitmap(null);
            screen3.setImageBitmap(null);
        } else if (colorPreferences == 2) {
            screen1.setImageBitmap(null);
            screen2.setImageResource(R.drawable.ic_done_black_18dp);
            screen3.setImageBitmap(null);
        } else if (colorPreferences == 3) {
            screen1.setImageBitmap(null);
            screen2.setImageBitmap(null);
            screen3.setImageResource(R.drawable.ic_done_black_18dp);
        }

        float min = pref.getFloat("minGoal", 2150);
        float max = pref.getFloat("maxGoal", 2735);

        validerSettings = (Button) findViewById(R.id.validerSettings);

        minGoal = (EditText) findViewById(R.id.minGoal);
        minGoal.setHint(Float.toString(min));
        maxGoal = (EditText) findViewById(R.id.maxGoal);
        maxGoal.setHint(Float.toString(max));
    }

    public void validerSettings(View button) {
        if(button == validerSettings) {
            SharedPreferences.Editor prefEdit = pref.edit();

            if (minGoal.getText().length() != 0) {
                float min = Float.parseFloat(minGoal.getText().toString());
                prefEdit.putFloat("minGoal", min);
            }
            if (maxGoal.getText().length() != 0) {
                float max = Float.parseFloat(maxGoal.getText().toString());
                prefEdit.putFloat("maxGoal", max);
            }

            prefEdit.apply();
            finish();
        }
    }

    public void changeBackground (View button) {
        SharedPreferences.Editor prefEdit = style.edit();
        if (button == screen1) {
            prefEdit.putInt("background", 1);
            prefEdit.apply();
            screen1.setImageResource(R.drawable.ic_done_black_18dp);
            screen2.setImageBitmap(null);
            screen3.setImageBitmap(null);
        }
        if (button == screen2) {
            prefEdit.putInt("background", 2);
            prefEdit.apply();
            screen1.setImageBitmap(null);
            screen2.setImageResource(R.drawable.ic_done_black_18dp);
            screen3.setImageBitmap(null);
        }
        if (button == screen3) {
            prefEdit.putInt("background", 3);
            prefEdit.apply();
            screen1.setImageBitmap(null);
            screen2.setImageBitmap(null);
            screen3.setImageResource(R.drawable.ic_done_black_18dp);
        }
    }
}

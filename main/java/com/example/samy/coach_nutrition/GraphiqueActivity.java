package com.example.samy.coach_nutrition;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphiqueActivity extends AppCompatActivity {

    private static final int ORIENTATION_0 = 0;

    NutritionContentResolver cr;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphique);

        cr = new NutritionContentResolver(this, NutritionContentResolver.HISTORIQUE_ID);
        pref = getSharedPreferences("DATA", Context.MODE_PRIVATE);


        float min = pref.getFloat("minGoal", 2150);
        float max = pref.getFloat("maxGoal", 2735);

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int screenOrientation = display.getRotation();

        Log.d("screen", "onCreate: "+Integer.toString(screenOrientation));

        if (screenOrientation == ORIENTATION_0) {
            BarChart chart = (BarChart) findViewById(R.id.chart);

            String[][] values = cr.getAllElements();
            List<BarEntry> entries = new ArrayList<BarEntry>();
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("0");

            for (int i = 0; i < cr.getTableSize(); i++) {
                Data element = new Data (i+1, Float.parseFloat(values[i][1]));
                entries.add(new BarEntry(element.getX(), element.getY()));
                labels.add(values[i][0].substring(0,4));
            }

            BarDataSet dataSet = new BarDataSet(entries, "Calorie(s)");
            dataSet.setColor(getResources().getColor(R.color.colorPrimary));
            dataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));

            BarData barData = new BarData(dataSet);
            chart.setData(barData);
            chart.animateY(3000);
            chart.setDescription(null);
            chart.setPinchZoom(false);
            chart.setScaleEnabled(false);
            chart.setDrawBarShadow(false);
            chart.setDrawGridBackground(false);
            chart.getAxisRight().setEnabled(false);
            chart.setVisibleYRangeMinimum(max + 200, YAxis.AxisDependency.LEFT);

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setValueFormatter(new LargeValueFormatter());
            leftAxis.setAxisMinimum(0f);

            LimitLine lmax = new LimitLine(max, "Max " + Float.toString(max));
            lmax.setLineWidth(4f);
            lmax.enableDashedLine(10f, 10f, 0f);
            lmax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            lmax.setTextSize(10f);
            lmax.setLineColor(getResources().getColor(R.color.max));

            LimitLine lmin = new LimitLine(min, "Min " + Float.toString(min));
            lmin.setLineWidth(4f);
            lmin.enableDashedLine(10f, 10f, 0f);
            lmin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            lmin.setTextSize(10f);
            lmin.setLineColor(getResources().getColor(R.color.min));

            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.addLimitLine(lmax);
            leftAxis.addLimitLine(lmin);

            chart.invalidate();
        } else {
            LineChart chart = (LineChart) findViewById(R.id.chart);

            String[][] values = cr.getAllElements();
            ArrayList<Entry> entries = new ArrayList<Entry>();
            ArrayList<String> labels = new ArrayList<String>();
            labels.add("0");

            for (int i = 0; i < cr.getTableSize(); i++) {
                Data element = new Data (i+1, Float.parseFloat(values[i][1]));
                entries.add(new Entry(element.getX(), element.getY()));
                labels.add(values[i][0].substring(0,4));
            }

            LineDataSet dataSet = new LineDataSet(entries, "Calorie(s)");
            dataSet.setColor(getResources().getColor(R.color.colorPrimary));
            dataSet.setValueTextColor(getResources().getColor(R.color.colorAccent));

            LineData lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.animateY(3000);
            chart.setDescription(null);
            chart.setPinchZoom(false);
            chart.setScaleEnabled(false);
            chart.setDrawGridBackground(false);
            chart.getAxisRight().setEnabled(false);
            chart.setVisibleYRangeMinimum(max + 200, YAxis.AxisDependency.LEFT);

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setValueFormatter(new LargeValueFormatter());
            leftAxis.setAxisMinimum(0f);

            LimitLine lmax = new LimitLine(max, "Max " + Float.toString(max));
            lmax.setLineWidth(4f);
            lmax.enableDashedLine(10f, 10f, 0f);
            lmax.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            lmax.setTextSize(10f);
            lmax.setLineColor(getResources().getColor(R.color.max));

            LimitLine lmin = new LimitLine(min, "Min " + Float.toString(min));
            lmin.setLineWidth(4f);
            lmin.enableDashedLine(10f, 10f, 0f);
            lmin.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            lmin.setTextSize(10f);
            lmin.setLineColor(getResources().getColor(R.color.min));

            leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
            leftAxis.addLimitLine(lmax);
            leftAxis.addLimitLine(lmin);

            chart.invalidate();

        }
    }
}

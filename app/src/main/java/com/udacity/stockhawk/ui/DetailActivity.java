package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private String mSymbol;
    private String mHistory;
    ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String labelText = getResources().getString(R.string.label_text);

        Intent intent = getIntent();
        if (intent != null){
            if(intent.hasExtra("Symbol")){
                mSymbol = intent.getStringExtra("Symbol");
            }
        }

        String subtitle = mSymbol;
        toolbar=getSupportActionBar();
        toolbar.setSubtitle(subtitle);

        Uri uri = Contract.Quote.makeUriForStock(mSymbol);
        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        ArrayList<Entry> entries = new ArrayList<Entry>();
        Calendar calendar = Calendar.getInstance();
        ArrayList<String> dates = new ArrayList<String>();


        while (cursor.moveToNext()) {
            mHistory = cursor.getString(cursor.getColumnIndex(Contract.Quote.COLUMN_HISTORY));
        }

        List<String> quotes = Arrays.asList(mHistory.split("\\r?\\n"));
        Collections.reverse(quotes);

        int i=0;
        for (String quote: quotes){
            String[] dateValues = quote.split(",");
            calendar.setTimeInMillis(Long.parseLong(dateValues[0]));

            String date =  calendar.get(Calendar.DAY_OF_MONTH) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.YEAR);
            dates.add(date);

            entries.add(new Entry((float)i, Float.parseFloat(dateValues[1])));
            i++;
        }

        LineChart chart = (LineChart) findViewById(R.id.history_chart);

        LineDataSet dataSet= new LineDataSet(entries, mSymbol);
        dataSet.setColor(Color.MAGENTA);
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);

        XAxis xAxis = chart.getXAxis();
        Object[] datesObject = dates.toArray();
        xAxis.setValueFormatter(new ValueFormatter(Arrays.copyOf(datesObject,datesObject.length,
                String[].class)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.RED);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setTextColor(Color.RED);
        Description description = new Description();
        description.setText(mSymbol + " " + labelText);
        description.setTextColor(Color.YELLOW);
        chart.setDescription(description);
        chart.animateY(2000);
        chart.setKeepPositionOnRotation(true);

        chart.invalidate();
    }

    public class ValueFormatter implements IAxisValueFormatter {
        private String[] mValue;

        public ValueFormatter(String[] value) {
            this.mValue = value;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValue[(int) value];
        }
    }
}

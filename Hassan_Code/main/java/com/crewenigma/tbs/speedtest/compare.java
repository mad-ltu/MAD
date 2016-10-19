package com.crewenigma.tbs.speedtest;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;

public class compare extends AppCompatActivity {

    TextView result;

    SQLiteDatabase mydatabase;
    static DataPoint[] points;
    static DataPoint[] points2;
    static int i=0;
    BarGraphSeries<DataPoint> series;
    BarGraphSeries<DataPoint> series2;
    Button displayGraph;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        result = (TextView) findViewById(R.id.result);
        result.setMovementMethod(new ScrollingMovementMethod());
        displayGraph=(Button)findViewById(R.id.display);
        //DataBase
        mydatabase  = openOrCreateDatabase("wifidetails",MODE_PRIVATE,null);

        Cursor resultSet = mydatabase.rawQuery("Select * from details", null);
        resultSet.moveToFirst();

        String value="";
        if (resultSet .moveToFirst()) {

            while (resultSet.isAfterLast() == false) {

                String name = resultSet.getString(0);
                String download = resultSet.getString(1);
                String upload = resultSet.getString(2);
                value+=name + "   " + download + "        " + upload+"\n";
                resultSet.moveToNext();
                i++;
            }
        }
        result.setText(value);

/*
        int i1=i;
        Log.i("value of i",""+i);
        int position=0;

        points=new DataPoint[i1];
        points2=new DataPoint[i1];

        Cursor resultSet2 = mydatabase.rawQuery("Select * from details", null);
        resultSet2.moveToFirst();
        if (resultSet2 .moveToFirst()) {

            while (resultSet2.isAfterLast() == false) {

                String name = resultSet2.getString(0);
                String download = resultSet2.getString(1);
                String upload = resultSet2.getString(2);
                float download1=(Float.parseFloat(download));
                float upload1=(Float.parseFloat(upload));

                points[position] = new DataPoint(position,download1);
                points2[position] = new DataPoint(position,upload1);
                position++;
                resultSet2.moveToNext();
            }
        }
        series2 = new BarGraphSeries<>(points2);
        series = new BarGraphSeries<>(points);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.RED;
            }
        });

        series2.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return BLUE;
            }
        });

        series.setSpacing(50);

        series2.setSpacing(50);

        // draw values on top
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.BLACK);

        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.BLACK);
        graph.setTitle("Speed Analysis");
        //series.setValuesOnTopSize(50);

        // enable scaling and scrolling

        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollable(true); // enables horizontal scrolling
        graph.getViewport().setScrollableY(true); // enables vertical scrolling
        graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
        graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling
        series.setColor(RED);
        series.setTitle("Dwon speed");
        series2.setColor(BLUE);
        series2.setTitle("Up speed");


        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setSpacing(5);
        graph.getLegendRenderer().setMargin(5);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        graph.addSeries(series2);
        graph.addSeries(series);



        for(int j=0;j<points.length;j++)
        {
            Log.i("points",""+points[j]);
        }
  */



  /*      while (result.hasSelection()){

            String name = resultSet.getString(0);
            String download = resultSet.getString(1);
            String upload = resultSet.getString(2);

            result.setText(name + " " + download + " " + upload);

        }
            */


    }
    public void show(View view)
    {
        Intent myintent=new Intent(this,Static_Analysis.class);
        startActivity(myintent);

    }
}

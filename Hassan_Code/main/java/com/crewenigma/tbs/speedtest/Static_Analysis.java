package com.crewenigma.tbs.speedtest;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.Arrays;

public class Static_Analysis extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    int hotspotCount;
    int wifiCount;
    int mobileCount;
    private String[] mMonth = new String[] {
            "Max", "Ave" , "Min"};

    SQLiteDatabase mydatabase;
    private static final String[]paths = {"Select","Top 5 HotSpot", "Wifi", "Mobile"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static__analysis);
        mydatabase  = openOrCreateDatabase("wifidetails",MODE_PRIVATE,null);

        spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(Static_Analysis.this,
                android.R.layout.simple_spinner_item,paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {


        switch (position) {
            case 1:

               Top5();
                // Whatever you want to happen when the first item gets selected
                break;
            case 2:

                wifi();
                // Whatever you want to happen when the second item gets selected
                break;
            case 3:
                mobile();
                Log.i("testing position",""+position);
                // Whatever you want to happen when the thrid item gets selected
                break;

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void Top5()
    {
        String selectQuery="SELECT * from details where name=?";
        Cursor resultSet = mydatabase.rawQuery(selectQuery, new String[]{"wifi"});
        hotspotCount = resultSet.getCount();
        float DmaxArray[]=new float[hotspotCount];
        float UmaxArray[]=new float[hotspotCount];
        int position2=0;
        //int count=0;

        Cursor resultSet2 = mydatabase.rawQuery(selectQuery, new String[]{"wifi"});
        resultSet2.moveToFirst();
        if (resultSet2 .moveToFirst()) {

            while (!resultSet2.isAfterLast()) {

                String name = resultSet2.getString(0);
                String download = resultSet2.getString(1);
                String upload = resultSet2.getString(2);
                float download1=(Float.parseFloat(download));
                float upload1=(Float.parseFloat(upload));
                DmaxArray[position2]=download1;
                UmaxArray[position2]=upload1;
                position2++;
                resultSet2.moveToNext();
            }
        }
        Arrays.sort(DmaxArray);
        Arrays.sort(UmaxArray);
        float averageDmax=0;
        float averageUmax=0;
        int breaking = 0;
        int k;
        for(k=DmaxArray.length-1; k >= 0; k--)
        {
            averageDmax+=DmaxArray[k];
            averageUmax+=UmaxArray[k];
            breaking++;
            if (breaking == 5)
                break;
        }
        averageDmax=averageDmax/5;
        averageUmax=averageUmax/5;
        k=DmaxArray.length-1;
        // Creating an  XYSeries for Income
        //CategorySeries incomeSeries = new CategorySeries("Income");
        XYSeries incomeSeries = new XYSeries("Upload");
        // Creating an  XYSeries for Income
        XYSeries expenseSeries = new XYSeries("Download");
        // Adding data to Income and Expense Series
        // for(int i=0;i<DmaxArray.length;i++){
        incomeSeries.add(0,UmaxArray[k]);
        expenseSeries.add(0,DmaxArray[k]);
        incomeSeries.add(1,averageDmax);
        expenseSeries.add(1,averageUmax);

        incomeSeries.add(2,UmaxArray[0]);
        expenseSeries.add(2,DmaxArray[0]);

        //}

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);


        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.rgb(130, 130, 230));
        incomeRenderer.setChartValuesSpacing(10);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.rgb(220, 80, 80));
        expenseRenderer.setChartValuesSpacing(10);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setBarSpacing(0.1);
        multiRenderer.setChartTitle("Top 5 HotSpot");
        //multiRenderer.setXTitle("Year 2012");
        multiRenderer.setYTitle("Speed");
        multiRenderer.setZoomButtonsVisible(true);

        for(int i=0; i<3;i++){
            multiRenderer.addXTextLabel(i,mMonth[i]);
        }


        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Start Activity
        startActivity(intent);
    }
    public void wifi()
    {
        String selectQuery="SELECT * from details where name=?";
        Cursor resultSet = mydatabase.rawQuery(selectQuery, new String[]{"wifi"});
        wifiCount = resultSet.getCount();
        resultSet.close();
        float DmaxArray[]=new float[wifiCount];
        float UmaxArray[]=new float[wifiCount];
        int position2=0;
        //int count=0;
        Cursor resultSet2 = mydatabase.rawQuery(selectQuery, new String[]{"wifi"});
        resultSet2.moveToFirst();
        if (resultSet2 .moveToFirst()) {

            while (!resultSet2.isAfterLast()) {

                String name = resultSet2.getString(0);
                String download = resultSet2.getString(1);
                String upload = resultSet2.getString(2);
                float download1=(Float.parseFloat(download));
                float upload1=(Float.parseFloat(upload));
                DmaxArray[position2]=download1;
                UmaxArray[position2]=upload1;
                position2++;
                resultSet2.moveToNext();
            }
        }

        // int[] x = { 0,1,2,3,4,5,6,7 };

        // Creating an  XYSeries for Income
        //CategorySeries incomeSeries = new CategorySeries("Income");
        XYSeries incomeSeries = new XYSeries("Upload");
        // Creating an  XYSeries for Income
        XYSeries expenseSeries = new XYSeries("Download");
        // Adding data to Income and Expense Series
        for(int i=0;i<DmaxArray.length;i++){
            incomeSeries.add(i,UmaxArray[i]);
            expenseSeries.add(i,DmaxArray[i]);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);


        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.rgb(130, 130, 230));
        incomeRenderer.setChartValuesSpacing(10);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.rgb(220, 80, 80));
        expenseRenderer.setChartValuesSpacing(10);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setBarSpacing(0.1);
        multiRenderer.setChartTitle("Wifi");
        //multiRenderer.setXTitle("Year 2012");
        multiRenderer.setYTitle("Speed");
        multiRenderer.setZoomButtonsVisible(true);
        for(int i = 0; i< wifiCount; i++){
            multiRenderer.addXTextLabel(i,""+i);
        }


        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Start Activity
        startActivity(intent);

        Log.i("testing position",""+ wifiCount);
    }

    public void mobile()
    {
        String selectQuery="SELECT * from details where name=?";
        Cursor resultSet = mydatabase.rawQuery(selectQuery, new String[]{"mobile"});
        mobileCount = resultSet.getCount();
        resultSet.close();
        float DmaxArray[]=new float[mobileCount];
        float UmaxArray[]=new float[mobileCount];
        int position2=0;
        //int count=0;
        Cursor resultSet2 = mydatabase.rawQuery(selectQuery, new String[]{"mobile"});
        resultSet2.moveToFirst();
        if (resultSet2 .moveToFirst()) {

            while (!resultSet2.isAfterLast()) {

                String name = resultSet2.getString(0);
                String download = resultSet2.getString(1);
                String upload = resultSet2.getString(2);
                float download1=(Float.parseFloat(download));
                float upload1=(Float.parseFloat(upload));
                DmaxArray[position2]=download1;
                UmaxArray[position2]=upload1;
                position2++;
                resultSet2.moveToNext();
            }
        }

        // int[] x = { 0,1,2,3,4,5,6,7 };

        // Creating an  XYSeries for Income
        //CategorySeries incomeSeries = new CategorySeries("Income");
        XYSeries incomeSeries = new XYSeries("Upload");
        // Creating an  XYSeries for Income
        XYSeries expenseSeries = new XYSeries("Download");
        // Adding data to Income and Expense Series
        for(int i=0;i<DmaxArray.length;i++){
            incomeSeries.add(i,UmaxArray[i]);
            expenseSeries.add(i,DmaxArray[i]);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
        // Adding Income Series to the dataset
        dataset.addSeries(incomeSeries);
        // Adding Expense Series to dataset
        dataset.addSeries(expenseSeries);


        // Creating XYSeriesRenderer to customize incomeSeries
        XYSeriesRenderer incomeRenderer = new XYSeriesRenderer();
        incomeRenderer.setColor(Color.rgb(130, 130, 230));
        incomeRenderer.setChartValuesSpacing(10);
        incomeRenderer.setFillPoints(true);
        incomeRenderer.setLineWidth(2);
        incomeRenderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize expenseSeries
        XYSeriesRenderer expenseRenderer = new XYSeriesRenderer();
        expenseRenderer.setColor(Color.rgb(220, 80, 80));
        expenseRenderer.setChartValuesSpacing(10);
        expenseRenderer.setFillPoints(true);
        expenseRenderer.setLineWidth(2);
        expenseRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
        multiRenderer.setXLabels(0);
        multiRenderer.setBarSpacing(0.1);
        multiRenderer.setChartTitle("Mobile");
        //multiRenderer.setXTitle("Year 2012");
        multiRenderer.setYTitle("Speed");
        multiRenderer.setZoomButtonsVisible(true);
        for(int i = 0; i< mobileCount; i++){
            multiRenderer.addXTextLabel(i,""+i);
        }


        // Adding incomeRenderer and expenseRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(incomeRenderer);
        multiRenderer.addSeriesRenderer(expenseRenderer);

        // Creating an intent to plot bar chart using dataset and multipleRenderer
        Intent intent = ChartFactory.getBarChartIntent(getBaseContext(), dataset, multiRenderer, BarChart.Type.DEFAULT);

        // Start Activity
        startActivity(intent);

        Log.i("testing position",""+ mobileCount);
    }
}

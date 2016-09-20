package com.crewenigma.tbs.speedtest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class compare extends AppCompatActivity {

    TextView result;
    SQLiteDatabase mydatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        result = (TextView) findViewById(R.id.result);

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
            }
        }

        result.setText(value);


  /*      while (result.hasSelection()){

            String name = resultSet.getString(0);
            String download = resultSet.getString(1);
            String upload = resultSet.getString(2);

            result.setText(name + " " + download + " " + upload);

        }
            */


    }
}

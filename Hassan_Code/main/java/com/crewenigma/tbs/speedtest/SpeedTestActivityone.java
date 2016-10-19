package com.crewenigma.tbs.speedtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import fr.bmartel.speedtest.ISpeedTestListener;
import fr.bmartel.speedtest.SpeedTestError;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;

public class SpeedTestActivityone extends AppCompatActivity  {

    //Instanciate SpeedTest class
    SpeedTestSocket speedTestSocket;
    //Create speed test task
    SpeedTestTask stt;

    Button btnTest;
    TextView downSpeed;
    TextView upSpeed;

    Activity ctx=this;


    float downloadSpeed=0,uploadingspeed=0;
    int downloadSpeedcounter=0,uploadingSpeedcounter=0;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test_activityone);

        //DataBase
        mydatabase  = openOrCreateDatabase("wifidetails",MODE_PRIVATE,null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS details(name VARCHAR,download VARCHAR,upload VARCHAR);");
       // mydatabase.execSQL("INSERT INTO details VALUES('Internet','Download Speed','Upload Speed');");

        //Instanciate speed test task
        stt = new SpeedTestTask();

        speedTestSocket = new SpeedTestSocket();
        //initialize speed test
        initTest(speedTestSocket);

        downSpeed = (TextView) findViewById(R.id.downSpeed);
        upSpeed =(TextView) findViewById(R.id.upSpeed);
        btnTest=(Button) findViewById(R.id.runDownSpeed);

        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stt.execute('D');
            }
        });
    }


    private void initTest(SpeedTestSocket sts){
        sts.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onDownloadPacketsReceived(long packetSize, float transferRateBps, float transferRateOps) {

             //   Log.e("test", "Download Speed : " + transferRateBps);
               // downSpeed.setText("" + transferRateBps);
                //reset the speed test task for the next test
             //   resetSTT();
              //  Log.e("test", "Start uploading");
                //execute the upload speed straight after the download speed
              //  stt.execute('U'); //this is optional (*)

              //  Toast.makeText(getBaseContext(),"speed "+transferRateBps,Toast.LENGTH_LONG).show();

            }

            @Override
            public void onDownloadProgress(float percent, SpeedTestReport report) { //live data
                //Output to logcat
               // Log.e("test", "aa gaya6");
                Log.e("Downloading", Float.toString(report.getTransferRateBit()) + " bps");

              final  SpeedTestReport r=report;
/*
                downloadSpeed += report.getTransferRateBit();
                downloadSpeedcounter++;
                if (downloadSpeedcounter>=5){
                    int speed = (int)downloadSpeed/downloadSpeedcounter;
                    Log.e("Downloading Speed: "," "+speed);
                }
*/
               // MainActivity mainActivity =new MainActivity();
              //  Intent intent=new Intent(getApplicationContext(),MainActivity.class);
             //   startActivity(intent);
               // downSpeed.setText("Downloading: " + Float.toString(report.getTransferRateBit()) + " bps");
                //Toast.makeText(ctx,"Downloading: " + Float.toString(report.getTransferRateBit()) + " bps",Toast.LENGTH_LONG).show();

                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(ctx,"Downloading: " + Float.toString(r.getTransferRateBit()) + " bps",Toast.LENGTH_LONG).show();
                        downloadingSpeed(r.getTransferRateBit());
                    }
                });

            }

            @Override
            public void onDownloadError(SpeedTestError speedTestError, String errorMessage) {
                Log.e("test","Error:  "+errorMessage);
            }

            @Override
            public void onUploadPacketsReceived(long packetSize, float transferRateBps, float transferRateOps) {
                //reset the speed test task for the next test
               // Log.e("test","aa gaya8");
                resetSTT();

            }

            @Override
            public void onUploadError(SpeedTestError speedTestError, String errorMessage) {
                Log.e("test", "aa gaya9" + errorMessage);
            }

            @Override
            public void onUploadProgress(float percent, SpeedTestReport report) { //live data
               // Log.e("test","aa gaya55");
                Log.e("Uploading: ", Float.toString(report.getTransferRateBit()) + " bps");

                final SpeedTestReport r=report;

                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadingSpeed(r.getTransferRateBit());
                    }
                });

            }
        });
    }

    private void resetSTT(){
        speedTestSocket.closeSocket();
        speedTestSocket.forceStopTask();
        stt.cancel(true); //cancel async task
        speedTestSocket = new SpeedTestSocket(); //initialize new speed test socket for next test
        initTest(speedTestSocket); //initialize new test (listeners etc) for next test
        stt = new SpeedTestTask(); //initialize new async task
    }



    public class SpeedTestTask extends AsyncTask<Character, Void, String> {

        @Override
        protected String doInBackground(Character... params) {
            Log.e("test","aa gaya56");
            char type = params[0];
            if(type=='D') {
                Log.e("test", "aa gaya me");


                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        downSpeed.setText("Calculating Downloading Speed");
                    }
                });

                speedTestSocket.startDownload("1.testdebit.info", 80, "/fichiers/10Mo.dat");




            } else if (type == 'U') {

                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        upSpeed.setText("Calculating Uploading Speed");
                    }
                });

                speedTestSocket.startUpload("1.testdebit.info", 80, "/", 1000000);

            }

            return null;
        }
    }


    void downloadingSpeed(float speed){

        downloadSpeed+=speed;
        downloadSpeedcounter++;
        if (downloadSpeedcounter==30) {
            downloadSpeed = downloadSpeed / downloadSpeedcounter;
            downSpeed.setText("Downloading Speed: " + (downloadSpeed/1024));
            resetSTT();
           // resetOurValue();
            stt.execute('U');
        }
    }

    void uploadingSpeed(float speed){
        uploadingspeed+=speed;
        uploadingSpeedcounter++;
        if (uploadingSpeedcounter==3){
            uploadingspeed=uploadingspeed/uploadingSpeedcounter;
            upSpeed.setText("Uploading Speed: " + (uploadingspeed/1024/1024));

           // Toast.makeText(getApplicationContext(),"Uploading Speed: "+uploadingspeed,Toast.LENGTH_LONG).show();
            resetSTT();
            String networkName=cheekstatus();
            if(networkName!=null)
            mydatabase.execSQL("INSERT INTO details VALUES('"+networkName+"','" + downloadSpeed/1024 + "','" + (uploadingspeed/1024/1024)+ "');");

            resetOurValue();
        }
    }

    void resetOurValue(){
        downloadSpeed=0;
        downloadSpeedcounter=0;
        uploadingspeed=0;
        uploadingSpeedcounter=0;
    }


    String  nameOfInternet(){
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            return networkInfo.getSubtypeName();

          // String name =  networkInfo.getSubtypeName();
        } else {
            // display error
            return "Internet";
        }
    }
    public String cheekstatus()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo Mobile=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if(wifi.isConnectedOrConnecting())
        {
            return "wifi";
        }
        if(Mobile.isConnectedOrConnecting())
        {
            return "mobile";
        }
        return null;
    }

   public void compare(View view){

       Intent intent = new Intent(this,compare.class);
       startActivity(intent);
   }

}

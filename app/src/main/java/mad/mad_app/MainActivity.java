package mad.mad_app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SpeedTest> entries = null;
    private ArrayAdapter<SpeedTest> adapter = null;

    private ListView listView = null;
    private ImageButton addButton = null;

    private Button btnGPS;

    private BackgroundAsync asyncTask = new BackgroundAsync();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        entries = new ArrayList<SpeedTest>();
        adapter = new ArrayAdapter<SpeedTest>(this, android.R.layout.simple_list_item_1, entries);

        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        btnGPS = (Button) findViewById(R.id.btnActivityGPS);
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GPSActivity.class);
                startActivity(intent);
            }
        });

        addButton = (ImageButton)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSpeedTest();
            }
        });
    }

    private void newSpeedTest() {
        asyncTask.execute();
    }

    private class BackgroundAsync extends AsyncTask<String, Double, Long> {

        static final long FILE_SIZE = 5 * 1024; // 5MB

        SpeedTest currentTest = null;

        @Override
        protected Long doInBackground(String... params) {
            try {
                URL downURL = new URL("http://download.thinkbroadband.com/5MB.zip");
                URLConnection conn = downURL.openConnection();

                long startTime = System.currentTimeMillis();
                long totalDownloaded = 0;

                BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                int read = 0;
                byte[] buf = new byte[1024];
                while ((read = bis.read(buf)) != -1) {
                    totalDownloaded += read;
                    // Publish percentage
                    publishProgress(((double)totalDownloaded/(double)FILE_SIZE) * 100.0);
                }

                long finishTime = System.currentTimeMillis();

                return finishTime - startTime;
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "ERROR DOWNLOADING FILE", Toast.LENGTH_LONG);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            addButton.setEnabled(false);
            currentTest = new SpeedTest();
        }

        @Override
        protected void onProgressUpdate(Double... progress) {
        }

        @Override
        protected void onPostExecute(Long result) {
            if(result != null) {
                currentTest.setSpeedMbps((double)FILE_SIZE/result);
                adapter.add(currentTest);
            }

            addButton.setEnabled(true);
        }
    }
}

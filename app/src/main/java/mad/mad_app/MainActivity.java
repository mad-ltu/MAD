package mad.mad_app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    private ListView listView = null;
    private ImageButton addButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listView);

        addButton = (ImageButton)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newSpeedTest();
            }
        });
    }

    private void newSpeedTest() {
        addButton.setEnabled(false);
    }

    private class BackgroundAsync extends AsyncTask<String, Double, Long> {

        static final long FILE_SIZE = 5 * 1024; // 5MB

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
                return -1L;
            }
        }

        @Override
        protected void onProgressUpdate(Double... progress) {
        }

        @Override
        protected void onPostExecute(Long result) {
            addButton.setEnabled(true);
        }
    }
}

package mad.mad_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> entries;
    private ArrayAdapter<String> adapter;

    private ListView listView;
    private ImageButton btnStartTest;

    private Button btnGPS;

    //PERMISSIONS
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            checkAndAskPermissions();
        } else {
            // Pre-Marshmallow
        }
        entries = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);

        listView = (ListView ) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        btnGPS = (Button) findViewById(R.id.btnActivityGPS);
        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GPSActivity.class);
                startActivity(intent);
            }
        });

        btnStartTest = (ImageButton) findViewById(R.id.addButton);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundAsync().execute();
            }
        });
    }

    private class BackgroundAsync extends AsyncTask<Integer, Double, Long> {

        private final Integer FILE_SIZE = 5 * 1024 * 1024; // 5MB

        private SpeedTest current;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            btnStartTest.setEnabled(false);
            current = new SpeedTest();
        }

        @Override
        protected Long doInBackground(Integer... params) {

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
                Log.e("MAD", e.getMessage());
                return null;
            }

        }

        @Override
        protected void onProgressUpdate(Double... progress) {
            // Show progress update
            Log.d("MAD", progress[0].toString());
        }

        @Override
        protected void onPostExecute(Long result) {
            super.onPostExecute(result);

            if(result != null) {
                current.setSpeedMbps(((double)FILE_SIZE / 1024 / 1024)/((double)result / 1000));
                adapter.add(current.toString());
            }
            btnStartTest.setEnabled(true);
        }
    }

//************** Multiple permissions ****************//

    /**
     * Call multiple Permissions
     */

    private void checkAndAskPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("NETWORK STATE");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE EXTERNAL STORAGE");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                if (Build.VERSION.SDK_INT >= 23) {
                    // Marshmallow+
                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                } else {
                    // Pre-Marshmallow
                }

                return;
            }
            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                // Pre-Marshmallow
            }

            return;
        }

    }

    /**
     * add Permissions
     *
     * @param permissionsList
     * @param permission
     * @return
     */
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false; // They've permanently rejected permission, can't ask anymore.
            }
        } else {
            // Pre-Marshmallow
        }

        return true;
    }

    /**
     * Permissions results
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and others
                if (perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ) {
                    // All Permissions Granted

                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
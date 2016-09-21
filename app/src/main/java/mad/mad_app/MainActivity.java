package mad.mad_app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements LocationListener{
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private ArrayList<String> entries;
    private ArrayAdapter<String> adapter;

    private ListView listView;
    private Button btnStartTest;

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

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

        btnStartTest = (Button) findViewById(R.id.addButton);
        btnStartTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStartTest.setEnabled(false);

                if(Build.VERSION.SDK_INT >= 23) {
                    if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, MainActivity.this, null);
                    }
                } else {
                    locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, MainActivity.this, null);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if(location != null) {
            if(Build.VERSION.SDK_INT >= 23) {
                if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.removeUpdates(this);
                }
            } else {
                locationManager.removeUpdates(this);
            }

            new BackgroundAsync(location).execute();
        }
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }
    @Override
    public void onProviderEnabled(String provider) { }
    @Override
    public void onProviderDisabled(String provider) { }

    private class BackgroundAsync extends AsyncTask<Integer, Double, Long> {

        private final Integer FILE_SIZE = 5 * 1024 * 1024; // 5MB

        private Location loc;
        private SpeedTest current;

        public BackgroundAsync(Location loc) {
            this.loc = loc;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            btnStartTest.setEnabled(false);

            current = new SpeedTest();
            current.setLat(loc.getLatitude());
            current.setLon(loc.getLongitude());

            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connMgr.getActiveNetworkInfo();
            String netType = network.getTypeName();
            String netSubType = network.getSubtypeName();

            if(netType.equals("WIFI")) {
                WifiManager wifiMgr = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                netSubType = wifiMgr.getConnectionInfo().getSSID();
            } else if (netType.equals("MOBILE")) {
                // Everything is already fine.
            } else {
                netType = "UNKNOWN";
                netSubType = "UNKNOWN";
            }

            current.setConnType(netType);
            current.setConnSubType(netSubType);
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

    // Permissions
    private void checkAndAskPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE))
            permissionsNeeded.add("NETWORK STATE");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE EXTERNAL STORAGE");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("ACCESS FINE LOCATION");

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION and others
                if (perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
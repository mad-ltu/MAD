package mad.mad_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CALLBACK_ID = 4040;

    private SpeedTestExpandableListAdapter adapter;
    private ExpandableListView listView;
    private List<LocationGroupListItem> groups = new ArrayList<>();
    private HashMap<LocationGroupListItem, List<SpeedTestListItem>> childMap = new HashMap<>();

    private TextView tvItemDetail;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastKnownLocation;

    private ProgressDialog progressDialog;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // on connect to GoogleApiClient
        requestLocationUpdatesIfAllowed();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.lastKnownLocation = location;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    protected synchronized GoogleApiClient buildGoogleApiClient() {
        return new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000); // 5s interval
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    @TargetApi(23)
    private boolean requestLocationUpdatesIfAllowed() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ) {
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                    locationRequest, this);
            return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            setContentView(R.layout.activity_main);

            // Marshmallow+ check for permissions
            checkAndAskPermissions();

            tvItemDetail = (TextView) findViewById(R.id.tvDetails);

            googleApiClient = buildGoogleApiClient();
            createLocationRequest();

            initList();

            adapter = new SpeedTestExpandableListAdapter(groups, childMap);
            listView = (ExpandableListView) findViewById(R.id.exListView);
            listView.setAdapter(adapter);

            listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    tvItemDetail.setText(((LocationGroupListItem) adapter.getGroup(groupPosition)).data.toString());
                    return false;
                }
            });

            listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    tvItemDetail.setText(((SpeedTestListItem) adapter.getChild(groupPosition, childPosition)).data.toString());
                    return false;
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        if (googleApiClient.isConnected()) {
            requestLocationUpdatesIfAllowed();
        }

        List<LocationGroupListItem> groupsCopy = new ArrayList<>(groups);
        Map<LocationGroupListItem, List<SpeedTestListItem>> childMapCopy = new HashMap<>(childMap);

        groups.clear();
        childMap.clear();

        initList();

        for (LocationGroupListItem groupCopy : groupsCopy) {
            int groupIndex = groups.indexOf(groupCopy);
            if (groupIndex != -1) {
                groups.get(groupIndex).cbSelector.setChecked(groupCopy.cbSelector.isChecked());

                for (SpeedTestListItem childCopy : childMapCopy.get(groupCopy)) {
                    List<SpeedTestListItem> newChildList = null;
                    for (LocationGroupListItem groupKey : childMap.keySet()) {
                        // Need to search manually because our hashcodes won't be the same.
                        if (groupKey.equals(groupCopy)) {
                            newChildList = childMap.get(groupKey);
                            break;
                        }
                    }

                    if (newChildList != null) {
                        int childIndex = newChildList.indexOf(childCopy);
                        if (childIndex != -1) {
                            newChildList.get(childIndex).cbSelector.setChecked(childCopy.cbSelector.isChecked());
                        }
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();

        tvItemDetail.setText("");

        super.onResume();
    }

    private void initList() {
        LocationGroupManager lgManager = new LocationGroupManager(this);
        SpeedTestManager stManager = new SpeedTestManager(this);
        CommentManager cManager = new CommentManager(this);
        try {
            lgManager.open();
            stManager.open();
            cManager.open();

            { // DEBUG!!!!!!
                // Clear dbs
//                lgManager.clearDB();
//                stManager.clearDB();
//                cManager.clearDB();
            } // DEBUG!!!!!!

            for(LocationGroup lg: lgManager.getAll()) {
                LocationGroupListItem uiLocationItem = new LocationGroupListItem(this, lg);
                groups.add(uiLocationItem);

                ArrayList<SpeedTestListItem> uiTestItems = new ArrayList<>();
                for(SpeedTest st: stManager.getAllForParent(lg.getId())) {
                    SpeedTestListItem uiTestItem = new SpeedTestListItem(this, st);
                    uiTestItems.add(uiTestItem);
                }

                childMap.put(uiLocationItem, uiTestItems);
            }

        } catch(Exception e) {
            Toast.makeText(this, "Error loading records from database!", Toast.LENGTH_LONG).show();
        } finally {
            lgManager.close();
            stManager.close();
            cManager.close();
        }
    }

    public void onNewTestClicked(View v) {
        new SpeedTestTask().execute();
    }

    private void onTestCompleted(SpeedTest test) {
        if(test.getLat() == null) {
            Toast.makeText(this, "Error recieving GPS data", Toast.LENGTH_LONG).show();
            return;
        }

        SpeedTestListItem li = new SpeedTestListItem(this, test);
        Location testLoc = new Location(LocationManager.GPS_PROVIDER);
        testLoc.setLatitude(test.getLat());
        testLoc.setLongitude(test.getLon());

        LocationGroupListItem foundGroup = null;
        for(LocationGroupListItem lgi: groups) {
            Location location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(lgi.data.getLat());
            location.setLongitude(lgi.data.getLon());
            if(location.distanceTo(testLoc) < 30) { // Anything within a 30m radius is considered to be the same location
                foundGroup = lgi;
                break;
            }
        }

        if(foundGroup == null) {
            LocationGroup newLoc = new LocationGroup("New Location", test.getLat(), test.getLon());
            LocationGroupManager lgm = new LocationGroupManager(this);
            try {
                lgm.open();
                newLoc.setId(lgm.insert(newLoc));

                foundGroup = new LocationGroupListItem(this, newLoc);
                groups.add(foundGroup);
            } catch (SQLException e) {
                Log.e(TAG, "Error inserting into location DB");
            } finally {
                lgm.close();
            }
        }

        SpeedTestManager stm = new SpeedTestManager(this);
        try {
            stm.open();
            test.setParentId(foundGroup.data.getId());
            test.setId(stm.insert(test));

            List<SpeedTestListItem> existing = childMap.get(foundGroup);
            if(existing == null) {
                existing = new ArrayList<>();
                childMap.put(foundGroup, existing);
            }

            li.cbSelector = new CheckBox(this);

            childMap.get(foundGroup).add(li);
        } catch(SQLException e) {
            Log.e(TAG, "Error inserting into test DB");
        } finally {
            stm.close();
        }

        adapter.notifyDataSetChanged();
    }

    public void onStatsClicked(View v) {
        List<LocationGroup> subListLocationPassthrough = new ArrayList<>();
        Map<LocationGroup, List<SpeedTest>> subMapTestPassthrough = new HashMap<>();

        // go through and find all items that are checked and pass them through
        for(LocationGroupListItem location : groups) {
            if(location.cbSelector.isChecked()) {
                // if the location is checked, all items under it are also checked
                subListLocationPassthrough.add(location.data);

                List<SpeedTest> childList = new ArrayList<>();
                for(SpeedTestListItem test : childMap.get(location)) {
                    childList.add(test.data);
                }
                subMapTestPassthrough.put(location.data, childList);
            } else {
                // A location can still have checked items if it isn't checked.
                // Still possibly need to add it
                List<SpeedTest> childList = new ArrayList<>();
                for(SpeedTestListItem test : childMap.get(location)) {
                    if(test.cbSelector.isChecked()) {
                        childList.add(test.data);
                    }
                }

                if(!childList.isEmpty()) {
                    subListLocationPassthrough.add(location.data);
                    subMapTestPassthrough.put(location.data, childList);
                }
            }
        }

        // There was nothing selected, that means we want everything
        if(subListLocationPassthrough.size() == 0) {
            for(LocationGroupListItem location : groups) {
                subListLocationPassthrough.add(location.data);

                List<SpeedTest> childList = new ArrayList<>();
                for(SpeedTestListItem test : childMap.get(location)) {
                    childList.add(test.data);
                }
                subMapTestPassthrough.put(location.data, childList);
            }
        }

        Intent statsIntent = new Intent(this, StatisticsActivity.class);
        Bundle data = new Bundle();
        data.putSerializable("LOCATION_LIST", (Serializable)subListLocationPassthrough);
        data.putSerializable("TEST_MAP", (Serializable)subMapTestPassthrough);
        statsIntent.putExtras(data);

        startActivity(statsIntent);
    }

    private class SpeedTestTask extends AsyncTask<Integer, Double, Long> {

        public final int DL_FILE_SIZE = 5 * 1024 * 1024; // 5MB

        private SpeedTest current;

        @Override
        protected void onPreExecute() {
            final SpeedTestTask runningTest = this;
            current = new SpeedTest();

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

            progressDialog = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setTitle("Performing Test");
            progressDialog.setMessage("Performing initial setup...");
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.setProgressNumberFormat(null);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    runningTest.cancel(true);
                }
            });
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected Long doInBackground(Integer[] params) {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // Do test
                try {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.setMessage("Downloading...");
                        }
                    });

                    URL downURL = new URL("http://download.thinkbroadband.com/5MB.zip");
                    URLConnection conn = downURL.openConnection();
                    long startTime = System.currentTimeMillis();
                    long totalDownloaded = 0;
                    BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                    int read = 0;
                    byte[] buf = new byte[1024];
                    while ((read = bis.read(buf)) != -1 && !isCancelled()) {
                        totalDownloaded += read;
                        // Publish percentage
                        publishProgress(((double)totalDownloaded/(double) DL_FILE_SIZE) * 100.0);
                    }

                    long finishTime = System.currentTimeMillis();


                    if(isCancelled()) {
                        return null;
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setMessage("Waiting for GPS position...");
                            }
                        });

                        int timeout = 0;
                        while(!isCancelled() && timeout < 5) {
                            if(lastKnownLocation.getTime() > 10000 || lastKnownLocation.getAccuracy() > 50) {
                                // reject location for being too old or inaccurate
                                timeout++;
                                Thread.sleep(1000); // sleep one sec and try again
                            } else {
                                current.setLat(lastKnownLocation.getLatitude());
                                current.setLon(lastKnownLocation.getLongitude());
                            }
                        }

                        // We timed out, just use the last known location
                        if(current.getLat() ==  null) {
                            current.setLat(lastKnownLocation.getLatitude());
                            current.setLon(lastKnownLocation.getLongitude());
                        }
                    }

                    return finishTime - startTime;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return null;
                }

            } else {
                Log.e(TAG, "Location permission denied");
                current = null;
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Double... progress) {
            if(progress != null && progress[0] != null) {
                progressDialog.setProgress(progress[0].intValue());
            }
        }

        @Override
        protected void onPostExecute(Long timeTaken) {
            progressDialog.dismiss();

            if(timeTaken != null) {
                // Kilobytes/second
                current.setSpeedKBps(((double)DL_FILE_SIZE/1024)/((double)timeTaken/1000));
                onTestCompleted(current);
            }

            super.onPostExecute(timeTaken);
        }
    }

    // -------- PERMISSIONS ---------------

    @TargetApi(23)
    private void checkAndAskPermissions() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if(!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add("ACCESS FINE LOCATION");
        }
        if(!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionsNeeded.add("WRITE EXTERNAL STORAGE");
        }
        if(!addPermission(permissionsList, Manifest.permission.INTERNET)) {
            permissionsNeeded.add("INTERNET");
        }
        if(!addPermission(permissionsList, Manifest.permission.ACCESS_NETWORK_STATE)) {
            permissionsNeeded.add("ACCESS NETWORK STATE");
        }
        if(!addPermission(permissionsList, Manifest.permission.ACCESS_WIFI_STATE)) {
            permissionsNeeded.add("ACCESS WIFI STATE");
        }

        if(permissionsList.size() > 0) {
            if(permissionsNeeded.size() > 0) {
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        PERMISSIONS_REQUEST_CALLBACK_ID);
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    PERMISSIONS_REQUEST_CALLBACK_ID);
        }
    }

    @TargetApi(23)
    private boolean addPermission(List<String> permissionList, String permission) {
        if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(permission);

            if(!shouldShowRequestPermissionRationale(permission)) {
                return false; //User has denied access permanently
            }
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSIONS_REQUEST_CALLBACK_ID:
                List<String> deniedPerms = new ArrayList<>();
                for(int i = 0; i < permissions.length; i++) {
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPerms.add(permissions[i]);
                    }
                }

                if(deniedPerms.size() > 0) {
                    // Something was denied
                    Toast.makeText(this, "Permissions denied: " + deniedPerms, Toast.LENGTH_LONG).show();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
    // -------- END PERMISSIONS ---------------
}

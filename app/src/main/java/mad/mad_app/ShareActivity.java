package mad.mad_app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ShareActivity extends AppCompatActivity {
    private BluetoothAdapter btAdapter;

    private List<LocationGroup> groupListDataToSend;
    private Map<LocationGroup, List<SpeedTest>> childMapDataToSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        groupListDataToSend = (List<LocationGroup>)getIntent().getSerializableExtra("LOCATION_LIST");
        childMapDataToSend = (Map<LocationGroup, List<SpeedTest>>)getIntent().getSerializableExtra("TEST_MAP");

        try {
            File exportFile = new File(Environment.getExternalStorageDirectory(), "Export.txt");
            PrintWriter writer = new PrintWriter(new FileOutputStream( exportFile, false));

            StringBuilder sb = new StringBuilder();
            for(LocationGroup group : groupListDataToSend) {
                sb.append("\nLocation: ");
                sb.append(group.getName());
                sb.append(" @ ");
                sb.append(String.format("%.2f, %.2f", group.getLat(), group.getLon()));
                sb.append("\n");

                ArrayList<SpeedTest> foundChild = null;
                for(LocationGroup lg: childMapDataToSend.keySet()) {
                    if(lg.equals(group)) {
                        foundChild = (ArrayList<SpeedTest>)childMapDataToSend.get(lg);
                        break;
                    }
                }

                if(foundChild != null) {
                    for(SpeedTest test: foundChild) {
                        sb.append("\tTest: ");
                        sb.append(test.getConnType());
                        sb.append(" - ");
                        sb.append(test.getConnSubType());
                        sb.append("\t");
                        sb.append(String.format("%.2fKB/s Down - ", test.getSpeedKBpsDown()));
                        sb.append(String.format("%.2fKB/s Up", test.getSpeedKBpsUp()));
                        sb.append("\t");
                        sb.append(new SimpleDateFormat("EEE, MMM d yyyy  hh:mm.ssa").format(test.getDateTime()));
                        sb.append("\n");
                    }
                }
            }

            writer.println(sb.toString());
            writer.close();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.setPackage("com.android.bluetooth");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(exportFile));

            startActivity(shareIntent);

            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Error while exporting, file not sent.", Toast.LENGTH_LONG).show();
        }
    }

}

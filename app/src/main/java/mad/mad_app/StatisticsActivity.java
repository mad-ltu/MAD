package mad.mad_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        List<LocationGroup> locations =
                (List<LocationGroup>)getIntent().getSerializableExtra("LOCATION_LIST");
        Map<LocationGroup, List<SpeedTest>> testMap =
                (Map<LocationGroup, List<SpeedTest>>)getIntent().getSerializableExtra("TEST_MAP");

        // Do the rest of the stats stuff here.
    }
}

package mad.mad_app;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.android.gms.games.stats.Stats;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String[] spinnerItems = {"All", "Top 5 Hotspots", "WiFi", "Mobile"};

    GoogleMap map;

    ListView statsList;
    List<StatsWrapper> statsWrappers;
    List<StatsWrapper> shownItems;

    Spinner statType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        statsWrappers = new ArrayList<>();

        Map<LocationGroup, List<SpeedTest>> passedMap =
                (Map<LocationGroup, List<SpeedTest>>)getIntent().getSerializableExtra("TEST_MAP");
        for(LocationGroup group: passedMap.keySet()) {
            for(SpeedTest test : passedMap.get(group)) {
                statsWrappers.add(new StatsWrapper(group, test));
            }
        }
        shownItems = new ArrayList<>(statsWrappers);

        statsList = (ListView) findViewById(R.id.statsList);
        statsList.setAdapter(new StatsListAdapter(shownItems));

        ArrayAdapter<String> spinnerAdapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        spinnerItems);


        statType = (Spinner) findViewById(R.id.statType);
        statType.setAdapter(spinnerAdapter);
        statType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                changeStatType((String)parent.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;

        updateMap();
    }

    private void updateMap() {
        if(map != null) {
            map.clear();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            for(StatsWrapper stat: shownItems) {
                LatLng position = new LatLng(stat.group.getLat(), stat.group.getLon());
                map.addMarker(new MarkerOptions().position(position).title(stat.group.getName()));
                builder.include(position);
            }

            LatLngBounds bounds = builder.build();
            map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(), 18));
        }
    }

    private void changeStatType(String statType) {
        shownItems.clear();

        switch(statType) {
            case "All":
                shownItems.addAll(statsWrappers);
                Collections.sort(shownItems, new Comparator<StatsWrapper>() {
                    @Override
                    public int compare(StatsWrapper lhs, StatsWrapper rhs) {
                        return -lhs.test.getSpeedKBpsDown().compareTo(rhs.test.getSpeedKBpsDown());
                    }
                });
                break;
            case "Top 5 Hotspots":
                List<HotSpot> hotSpots = new ArrayList<>();

                for(StatsWrapper stat: statsWrappers) {
                    if(stat.test.getConnType().equals("WIFI")) {
                        HotSpot hsFound = null;
                        for(HotSpot hs : hotSpots) {
                            if(hs.name.equals(stat.test.getConnSubType())) {
                                hsFound = hs;
                                break;
                            }
                        }

                        if(hsFound == null) {
                            hsFound = new HotSpot();
                            hsFound.name = stat.test.getConnSubType();
                            hotSpots.add(hsFound);
                        }

                        double thisSpeed = stat.test.getSpeedKBpsDown();
                        if(thisSpeed > hsFound.maxSpeed) {
                            hsFound.maxSpeed = thisSpeed;
                        }
                        if(thisSpeed < hsFound.minSpeed) {
                            hsFound.minSpeed = thisSpeed;
                        }
                        hsFound.speeds.add(thisSpeed);
                    }
                }

                for(HotSpot hs: hotSpots) {
                    hs.calcAvgSpeed();
                }

                Collections.sort(hotSpots, new Comparator<HotSpot>() {
                    @Override
                    public int compare(HotSpot lhs, HotSpot rhs) {
                        return -lhs.avgSpeed.compareTo(rhs.avgSpeed);
                    }
                });

                if(hotSpots.size() > 5) {
                    hotSpots = hotSpots.subList(0, 5);
                }

                ArrayAdapter<String> hotSpotAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
                for(HotSpot hs: hotSpots) {
                    hotSpotAdapter.add(String.format("%s\nMin: %.2fKB/s\nMax: %.2fKB/s\nAvg: %.2fKB/s",
                            hs.name, hs.maxSpeed, hs.minSpeed, hs.avgSpeed));
                }
                statsList.setAdapter(hotSpotAdapter);
                return;
            case "WiFi":
                for(StatsWrapper stat: statsWrappers) {
                    if(stat.test.getConnType().equals("WIFI")) {
                        shownItems.add(stat);
                    }
                }
                Collections.sort(shownItems, new Comparator<StatsWrapper>() {
                    @Override
                    public int compare(StatsWrapper lhs, StatsWrapper rhs) {
                        return -lhs.test.getSpeedKBpsDown().compareTo(rhs.test.getSpeedKBpsDown());
                    }
                });
                break;
            case "Mobile":
                for(StatsWrapper stat: statsWrappers) {
                    if(stat.test.getConnType().equals("MOBILE")) {
                        shownItems.add(stat);
                    }
                }
                Collections.sort(shownItems, new Comparator<StatsWrapper>() {
                    @Override
                    public int compare(StatsWrapper lhs, StatsWrapper rhs) {
                        return -lhs.test.getSpeedKBpsDown().compareTo(rhs.test.getSpeedKBpsDown());
                    }
                });
                break;
        }

        statsList.setAdapter(new StatsListAdapter(shownItems));
    }
}

package mad.mad_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Tim on 25/09/2016.
 */
public class SpeedTestExpandableListAdapter extends BaseExpandableListAdapter {

    private List<LocationGroup> groups;
    private HashMap<LocationGroup, List<SpeedTest>> childMap;

    public SpeedTestExpandableListAdapter() {
        groups = new ArrayList<>();
        childMap = new HashMap<>();
    }

    public SpeedTestExpandableListAdapter(List<LocationGroup> groups, HashMap<LocationGroup, List<SpeedTest>> childMap) {
        this.groups = groups;
        this.childMap = childMap;
    }

    public void setBackingSources(List<LocationGroup> groups, HashMap<LocationGroup, List<SpeedTest>> childMap) {
        if(groups != null && childMap != null) {
            this.groups = groups;
            this.childMap = childMap;
        }
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((List<SpeedTest>)childMap.get(groups.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((List<SpeedTest>)childMap.get(groups.get(groupPosition))).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.location_list_item, null);
        }

        LocationGroup locationGroup = (LocationGroup)getGroup(groupPosition);
        TextView locationName = (TextView)convertView.findViewById(R.id.locationName);
        locationName.setText(locationGroup.getName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.test_list_item, null);
        }

        SpeedTest test = (SpeedTest)getChild(groupPosition, childPosition);

        TextView dateTime = (TextView)convertView.findViewById(R.id.dateTime);
        dateTime.setText(new SimpleDateFormat("EEE, MMM d ''yy HH:mm").format(test.getDateTime()));
        TextView speed = (TextView)convertView.findViewById(R.id.speed);
        speed.setText(test.getSpeedKbps() + "Kb/s");

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

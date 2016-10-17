package mad.mad_app;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tim on 25/09/2016.
 */
public class SpeedTestExpandableListAdapter extends BaseExpandableListAdapter {

    private List<LocationGroupListItem> groups;
    private HashMap<LocationGroupListItem, List<SpeedTestListItem>> childMap;

    public SpeedTestExpandableListAdapter() {
        groups = new ArrayList<>();
        childMap = new HashMap<>();
    }

    public SpeedTestExpandableListAdapter(List<LocationGroupListItem> groups, HashMap<LocationGroupListItem, List<SpeedTestListItem>> childMap) {
        this.groups = groups;
        this.childMap = childMap;
    }

    public void setBackingSources(List<LocationGroupListItem> groups, HashMap<LocationGroupListItem, List<SpeedTestListItem>> childMap) {
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
        return ((List<SpeedTestListItem>)childMap.get(groups.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMap.get(groups.get(groupPosition)).get(childPosition);
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

        final int groupPosInner = groupPosition;
        final ViewGroup childParentInner = parent;

        final LocationGroupListItem locationGroup = (LocationGroupListItem)getGroup(groupPosition);

        locationGroup.tvLocationName = (TextView)convertView.findViewById(R.id.locationName);
        locationGroup.tvLocationName.setText(locationGroup.data.getName());

        locationGroup.btnEdit = (ImageButton) convertView.findViewById(R.id.btnEdit);
        locationGroup.btnEdit.setFocusable(false);
        locationGroup.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(childParentInner.getContext(), CommentsActivity.class);
                commentsIntent.putExtra("ID", locationGroup.data.getId());
                commentsIntent.putExtra("PARENT_TYPE_CODE", "LOCATION_GROUP");
                commentsIntent.putExtra("INFO", locationGroup.data.getName());
                commentsIntent.putExtra("EXTRA_INFO_LEFT",
                        String.format("%.2f, %.2f", locationGroup.data.getLat(), locationGroup.data.getLon()));
                commentsIntent.putExtra("EXTRA_INFO_RIGHT", "");

                childParentInner.getContext().startActivity(commentsIntent);
            }
        });

        locationGroup.cbSelector = (CheckBox) convertView.findViewById(R.id.cbSelector);
        locationGroup.cbSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This happens before the isChecked property is updated
                boolean newCheckState = locationGroup.cbSelector.isChecked();
                for(int i = 0; i < getChildrenCount(groupPosInner); i++) {
                    SpeedTestListItem listItem = (SpeedTestListItem) getChild(groupPosInner, i);
                    listItem.cbSelector.setChecked(newCheckState);
                }
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.test_list_item, null);
        }

        final SpeedTestListItem test = (SpeedTestListItem) getChild(groupPosition, childPosition);

        test.tvDateTime = (TextView)convertView.findViewById(R.id.dateTime);
        test.tvDateTime.setText(new SimpleDateFormat("EEE, MMM d yyyy \nhh:mm.ssa").format(test.data.getDateTime()));

        test.tvSpeed = (TextView)convertView.findViewById(R.id.speed);
        test.tvSpeed.setText(String.format("%.2fKB/s", test.data.getSpeedKBps()));

        final ViewGroup childParentInner = parent;

        test.btnEdit = (ImageButton) convertView.findViewById(R.id.btnEdit);
        test.btnEdit.setFocusable(false);
        test.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(childParentInner.getContext(), CommentsActivity.class);
                commentsIntent.putExtra("ID", test.data.getId());
                commentsIntent.putExtra("PARENT_ID", test.data.getParentId());
                commentsIntent.putExtra("PARENT_TYPE_CODE", "SPEED_TEST");
                commentsIntent.putExtra("INFO", new SimpleDateFormat("dd/mm/yyyy hh:mm.ssa").format(test.data.getDateTime()));
                commentsIntent.putExtra("EXTRA_INFO_LEFT", test.data.getConnType() + " - " + test.data.getConnSubType());
                commentsIntent.putExtra("EXTRA_INFO_RIGHT", test.data.getSpeedKBps());

                childParentInner.getContext().startActivity(commentsIntent);
            }
        });

        boolean checked = false;
        if(test.cbSelector != null) {
            checked = test.cbSelector.isChecked();
        }
        test.cbSelector = (CheckBox) convertView.findViewById(R.id.cbSelector);
        test.cbSelector.setChecked(checked);
        test.cbSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LocationGroupListItem ourGroup = groups.get(groupPosition);
                if(ourGroup.cbSelector.isChecked() && !isChecked) {
                    ourGroup.cbSelector.setChecked(false);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void notifyDataSetChanged() {
        // want to sort the tests by speed descending whenever something is changed.
        for(LocationGroupListItem lgItem : groups) {
            Collections.sort(childMap.get(lgItem), new Comparator<SpeedTestListItem>() {
                @Override
                public int compare(SpeedTestListItem lhs, SpeedTestListItem rhs) {
                    if(lhs.data.getSpeedKBps() != null && rhs.data.getSpeedKBps() != null) {
                        return rhs.data.getSpeedKBps().compareTo(lhs.data.getSpeedKBps());
                    }

                    return  -1;
                }
            });
        }

        super.notifyDataSetChanged();
    }
}

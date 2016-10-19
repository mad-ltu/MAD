package mad.mad_app;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Tim on 19/10/2016.
 */

public class StatsListAdapter implements ListAdapter {
    private List<StatsWrapper> tests;

    public StatsListAdapter() { }

    public StatsListAdapter(List<StatsWrapper> tests) {
        this.tests = tests;
    }

    public void setBackingSource(List<StatsWrapper> tests) {
        this.tests = tests;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return tests.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.stats_list_item, null);
        }

        StatsWrapper test = tests.get(position);

        TextView tvLocationName = (TextView) convertView.findViewById(R.id.tvLocationName);
        TextView tvDateTime = (TextView) convertView.findViewById(R.id.tvDateTime);
        TextView tvNetwork = (TextView) convertView.findViewById(R.id.tvNetwork);
        TextView tvSpeed = (TextView) convertView.findViewById(R.id.tvSpeed);

        tvLocationName.setText(test.group.getName());
        tvDateTime.setText(new SimpleDateFormat("dd/MM/yyyy @ hh:mm.ssa").format(test.test.getDateTime()));
        tvNetwork.setText(test.test.getConnSubType());
        tvSpeed.setText(String.format("%.2fKB/s", test.test.getSpeedKBpsDown()));


        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return tests.isEmpty();
    }
}

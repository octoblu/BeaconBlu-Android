package com.octoblu.beaconblu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class BeaconAdapter extends ArrayAdapter<BeaconInfo> {
    public BeaconAdapter(Context context, ArrayList<BeaconInfo> beacons) {
        super(context, 0, beacons);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BeaconInfo beacon = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_beacon_row, parent, false);
        }
        // Lookup view for data population
        TextView uuid = (TextView) convertView.findViewById(R.id.uuid);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        final CheckBox status = (CheckBox) convertView.findViewById(R.id.status_checkbox);
        // Populate the data into the template view using the data object
        uuid.setText(beacon.uuid);
        name.setText(beacon.name);
        status.setChecked(beacon.status);
        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg) {
                final boolean checked = status.isChecked();
                onCheckboxClicked(position, checked);
            }
        });
        // Return the completed view to render on screen
        return convertView;
    }

    public void onCheckboxClicked(int position, Boolean checked) {
        // Is the view now checked?
        BeaconsActivity context = (BeaconsActivity) getContext();
        BeaconInfo beacon = getItem(position);
        context.setBeaconStatus(beacon.uuid, checked);
    }
}
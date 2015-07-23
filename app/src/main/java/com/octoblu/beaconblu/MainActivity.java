package com.octoblu.beaconblu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import java.util.Map;


public class MainActivity extends Activity {
    private BeaconApplication application;
    private Map<String, Boolean> beaconTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (BeaconApplication) getApplication();
        beaconTypes = application.getBeaconTypes();

        setCheckedBoxes();
    }

    private void setCheckedBoxes(){
        CheckBox estimote = (CheckBox) findViewById(R.id.checkbox_estimote);
        estimote.setChecked(beaconTypes.get(MeshbluBeacon.BEACON_TYPES.ESTIMOTE));
        CheckBox ibeacon = (CheckBox) findViewById(R.id.checkbox_ibeacon);
        ibeacon.setChecked(beaconTypes.get(MeshbluBeacon.BEACON_TYPES.IBEACON));
        CheckBox easibeacon = (CheckBox) findViewById(R.id.checkbox_easibeacon);
        easibeacon.setChecked(beaconTypes.get(MeshbluBeacon.BEACON_TYPES.EASIBEACON));
        CheckBox altbeacon = (CheckBox) findViewById(R.id.checkbox_altbeacon);
        altbeacon.setChecked(beaconTypes.get(MeshbluBeacon.BEACON_TYPES.ALTBEACON));
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkbox_estimote:
                beaconTypes.put(MeshbluBeacon.BEACON_TYPES.ESTIMOTE, checked);
                break;
            case R.id.checkbox_ibeacon:
                beaconTypes.put(MeshbluBeacon.BEACON_TYPES.IBEACON, checked);
                break;
            case R.id.checkbox_easibeacon:
                beaconTypes.put(MeshbluBeacon.BEACON_TYPES.EASIBEACON, checked);
                break;
            case R.id.checkbox_altbeacon:
                beaconTypes.put(MeshbluBeacon.BEACON_TYPES.IBEACON, checked);
                break;
        }

        application.setBeaconTypes(beaconTypes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

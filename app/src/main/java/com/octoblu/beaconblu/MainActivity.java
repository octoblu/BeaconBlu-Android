package com.octoblu.beaconblu;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.github.nkzawa.emitter.Emitter;

import org.altbeacon.beacon.Beacon;
import org.json.JSONObject;

import java.util.Map;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private BeaconApplication application;
    private Map<String, Boolean> beaconTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (BeaconApplication) getApplication();

        application.on(BeaconApplication.WHOAMI, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                SaneJSONObject jsonObject = (SaneJSONObject) args[0];
                if(jsonObject.getStringOrNull("owner") == null){
                    showOrHideClaimInfo(true);
                }else{
                    showOrHideClaimInfo(false);
                }
            }
        });
        application.on(BeaconApplication.CLAIM_GATEBLU, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String uuid = (String) args[0];
                String token = (String) args[1];
                claimDeviceInOctoblu(uuid, token);
            }
        });
        beaconTypes = application.getBeaconTypes();

        setCheckedBoxes();
        showOrHideClaimInfo(false);
    }

    public void claimBeaconBlu(View view){
        Log.d(TAG, "I wants to claim gateblu");
        application.claimDevice();
    }

    private void claimDeviceInOctoblu(String uuid, String token){
        String url = String.format("https://app.octoblu.com/node-wizard/claim/%s/%s", uuid, token);
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
        application.whoami();
    }

    private void showOrHideClaimInfo(Boolean show){
        LinearLayout claimInfo = (LinearLayout) findViewById(R.id.claim_beacon_blu);
        if(show){
            claimInfo.setVisibility(LinearLayout.VISIBLE);
        }else{
            claimInfo.setVisibility(LinearLayout.GONE);
        }
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
                beaconTypes.put(MeshbluBeacon.BEACON_TYPES.ALTBEACON, checked);
                break;
        }

        application.setBeaconTypes(beaconTypes);

        LinearLayout closeApplication = (LinearLayout)findViewById(R.id.close_application);
        closeApplication.setVisibility(LinearLayout.VISIBLE);
    }

    public void openBeaconsActivity(View view){
        Intent intent = new Intent(this, BeaconsActivity.class);
        MainActivity.this.startActivity(intent);
    }

    public void closeApplication(View view){
        System.exit(1);
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

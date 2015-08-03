package com.octoblu.beaconblu;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BeaconsActivity extends Activity {
    private static final String TAG = "BeaconsActivity";
    ListView listView;
    private BeaconApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        application = (BeaconApplication) getApplication();
        createList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_beacons, menu);
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

    public void setBeaconStatus(String uuid, Boolean status){
        SaneJSONObject beaconInfo = application.getBeaconInfo(application.getAllBeaconInfo(), uuid);
        beaconInfo.putBooleanOrIgnore("status", status);
        application.setBeaconInfo(uuid, beaconInfo);
    }

    private ArrayList<BeaconInfo> getBeaconInfo(){
        SaneJSONObject beaconInfo = application.getAllBeaconInfo();
        Iterator<String> uuids = beaconInfo.keys();
        ArrayList<BeaconInfo> beacons = new ArrayList();
        while(uuids.hasNext()){
            String uuid = uuids.next();
            SaneJSONObject jsonObject = beaconInfo.getJSONOrNull(uuid);
            jsonObject.putOrIgnore("uuid", uuid);
            Log.d(TAG, String.format("Beacon added to array %s", uuid));
            beacons.add(new BeaconInfo(jsonObject));
        }
        return beacons;
    }

    private void updateList(){
        BeaconAdapter adapter = (BeaconAdapter) listView.getAdapter();
        adapter.clear();
        adapter.addAll(getBeaconInfo());
    }

    private void createList(){
        listView = (ListView) findViewById(R.id.beacon_list);
        BeaconAdapter adapter = new BeaconAdapter(this, getBeaconInfo());
        listView.setAdapter(adapter);

        application.on(BeaconApplication.BEACONS_CHANGED, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList();
                    }
                });

            }
        });
    }

}

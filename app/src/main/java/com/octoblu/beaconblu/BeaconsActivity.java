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
import android.widget.ListView;
import android.widget.Toast;

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

    private void createList(){
        listView = (ListView) findViewById(R.id.beacon_list);

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

        BeaconAdapter adapter = new BeaconAdapter(this, beacons);
        listView.setAdapter(adapter);
    }
}

package com.octoblu.beaconblu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;

import java.util.ArrayList;
import java.util.Iterator;


public class BeaconsActivity extends Activity {
    private static final String TAG = "BeaconsActivity";
    ListView listView;
    private BeaconApplication application;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacons);

        application = (BeaconApplication) getApplication();

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.beacon_swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateOrCreateList();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        listView = (ListView) findViewById(R.id.beacon_list);

        updateOrCreateList();
        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Log.i(TAG, "Refreshing list view...");
                    updateOrCreateList();
                }
            }, 10000);
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
        BeaconInfo beaconInfo = application.getBeaconInfo(uuid);
        beaconInfo.status = status;
        application.setBeaconInfo(uuid, beaconInfo.toJSON());
    }

    public void setSensitivity(String uuid, Integer sensitivity){
        BeaconInfo beaconInfo = application.getBeaconInfo(uuid);
        beaconInfo.sensitivity = sensitivity;
        application.setBeaconInfo(uuid, beaconInfo.toJSON());
    }

    public ArrayList<BeaconInfo> getBeaconInfo(){
        ArrayList<BeaconInfo> beacons = application.getCurrentBeaconInfo();
        ArrayList<BeaconInfo> currentList = new ArrayList();
        Iterator<BeaconInfo> iter = beacons.iterator();
        while(iter.hasNext()){
            BeaconInfo info = iter.next();
            if(info.hasChangedRecently()){
                currentList.add(info);
            }
        }
        return currentList;
    }

    private void updateList(){
        BeaconAdapter adapter = (BeaconAdapter) listView.getAdapter();
        adapter.clear();
        adapter.addAll(getBeaconInfo());
    }

    private void updateOrCreateList(){
        BeaconAdapter adapter = new BeaconAdapter(this, getBeaconInfo());
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.empty_list));

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

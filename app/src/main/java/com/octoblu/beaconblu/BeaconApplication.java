package com.octoblu.beaconblu;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;

import org.altbeacon.beacon.Beacon;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.octoblu.beaconblu.MeshbluBeacon.BEACON_TYPES.ALTBEACON;
import static com.octoblu.beaconblu.MeshbluBeacon.BEACON_TYPES.EASIBEACON;
import static com.octoblu.beaconblu.MeshbluBeacon.BEACON_TYPES.ESTIMOTE;
import static com.octoblu.beaconblu.MeshbluBeacon.BEACON_TYPES.IBEACON;


public class BeaconApplication extends Application {
    private static final String TAG = "BeaconApplication";
    private static final String PREFERENCES_FILE_NAME = "meshblu_preferences";
    private static final String BEACON_STATUSES_KEY = "beacon_statuses";
    private static final String UUID = "uuid";
    private static final String TOKEN = "token";
    private MeshbluBeacon meshbluBeacon;

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting Beacon Application");
        startBeacon();
    }

    public void startBeacon(){
        Log.d(TAG, "Starting Meshblu Beacon");
        meshbluBeacon = new MeshbluBeacon(BeaconApplication.this.getCredentials(), this);
        meshbluBeacon.on(MeshbluBeacon.EVENTS.REGISTER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Registered emitted event");
                JSONObject deviceJSON = (JSONObject) args[0];
                saveCredentials(new SaneJSONObject().fromJSONObject(deviceJSON));
            }
        });
        meshbluBeacon.on(MeshbluBeacon.EVENTS.LOCATION_UPDATE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Location update happened!");
            }
        });

        meshbluBeacon.on(MeshbluBeacon.EVENTS.DISCOVERED_BEACON, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Discovered beacon");
                Beacon beacon = (Beacon) args[0];
                setBeaconStatus(beacon, true);
            }
        });

        meshbluBeacon.start(getBeaconTypeList());
        loadBeaconStatuses();
    }

    private SharedPreferences.Editor getPreferencesEditor() {
        return getSharedPreferences(PREFERENCES_FILE_NAME, 0).edit();
    }

    public List<String> getBeaconTypeList(){
        Map<String, Boolean> beaconTypes = getBeaconTypes();
        List<String> types = new ArrayList();
        if(beaconTypes.get(ESTIMOTE)){
            types.add(ESTIMOTE);
        }
        if(beaconTypes.get(IBEACON)){
            types.add(IBEACON);
        }
        if(beaconTypes.get(EASIBEACON)){
            types.add(EASIBEACON);
        }
        if(beaconTypes.get(ALTBEACON)){
            types.add(ALTBEACON);
        }
        if(beaconTypes.get(ALTBEACON)){
            types.add(ALTBEACON);
        }
        return types;
    }

    public Map<String, Boolean> getBeaconTypes(){
        SharedPreferences preferences = getSharedPreferences(BeaconApplication.PREFERENCES_FILE_NAME, 0);
        Map<String, Boolean> types = new HashMap();
        types.put(ESTIMOTE, preferences.getBoolean(ESTIMOTE, false));
        types.put(IBEACON, preferences.getBoolean(IBEACON, false));
        types.put(EASIBEACON, preferences.getBoolean(EASIBEACON, false));
        types.put(ALTBEACON, preferences.getBoolean(ALTBEACON, false));
        return types;
    }

    public void setBeaconTypes(Map<String, Boolean> types){
        SharedPreferences.Editor preferences = getPreferencesEditor();
        preferences.putBoolean(ESTIMOTE, types.get(ESTIMOTE));
        preferences.putBoolean(IBEACON, types.get(IBEACON));
        preferences.putBoolean(EASIBEACON, types.get(EASIBEACON));
        preferences.putBoolean(ALTBEACON, types.get(ALTBEACON));
        preferences.commit();
    }

    private SaneJSONObject getBeaconStatuses(){
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_FILE_NAME, 0);
        String jsonString = preferences.getString(BEACON_STATUSES_KEY, "{}");
        SaneJSONObject jsonObject = SaneJSONObject.fromString(jsonString);
        return jsonObject;
    }

    public void loadBeaconStatuses(){
        SaneJSONObject jsonObject = getBeaconStatuses();
        Iterator<String> uuids = jsonObject.keys();
        while(uuids.hasNext()){
            String uuid = uuids.next();
            meshbluBeacon.setBeaconStatus(uuid, jsonObject.getBoolean(uuid, false));
        }
    }

    private void setBeaconStatus(Beacon beacon, Boolean status){
        String uuid = beacon.getId1().toString();
        SharedPreferences.Editor preferences = getPreferencesEditor();
        SaneJSONObject jsonObject = getBeaconStatuses();
        jsonObject.putBooleanOrIgnore(uuid, status);
        preferences.putString(BEACON_STATUSES_KEY, jsonObject.toString());
        preferences.commit();
        meshbluBeacon.setBeaconStatus(uuid, status);
    }

    private SaneJSONObject getCredentials(){
        SaneJSONObject meshbluConfig = new SaneJSONObject();
        SharedPreferences preferences = getSharedPreferences(BeaconApplication.PREFERENCES_FILE_NAME, 0);
        String uuid = preferences.getString(BeaconApplication.UUID, null);
        String token = preferences.getString(BeaconApplication.TOKEN, null);
        Log.d(TAG, String.format("Credentials %s %s", uuid, token));
        meshbluConfig.putOrIgnore("uuid", uuid);
        meshbluConfig.putOrIgnore("token", token);
        return meshbluConfig;
    }

    private void saveCredentials(SaneJSONObject gatebluJSON) {
        String uuid = gatebluJSON.getStringOrNull("uuid");
        String token = gatebluJSON.getStringOrNull("token");

        SharedPreferences.Editor preferences = getPreferencesEditor();
        preferences.clear();
        preferences.putString(UUID, uuid);
        preferences.putString(TOKEN, token);
        preferences.commit();
    }
}


package com.octoblu.beaconblu;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONObject;


public class BeaconApplication extends Application {
    private static final String TAG = "BeaconApplication";
    private static final String PREFERENCES_FILE_NAME = "meshblu_preferences";
    private static final String UUID = "uuid";
    private static final String TOKEN = "token";
    private MeshbluBeacon meshbluBeacon;


    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Starting Beacon Application");
        meshbluBeacon = new MeshbluBeacon(BeaconApplication.this.getCredentials(), this);
        meshbluBeacon.on(MeshbluBeacon.REGISTER, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Registered emitted event");
                JSONObject deviceJSON = (JSONObject) args[0];
                saveCredentials(new SaneJSONObject().fromJSONObject(deviceJSON));
            }
        });
        meshbluBeacon.on(MeshbluBeacon.LOCATION_UPDATE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d(TAG, "Location update happened!");
            }
        });
        meshbluBeacon.start("Estimote");
    }

    private SharedPreferences.Editor getPreferencesEditor() {
        return getSharedPreferences(PREFERENCES_FILE_NAME, 0).edit();
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


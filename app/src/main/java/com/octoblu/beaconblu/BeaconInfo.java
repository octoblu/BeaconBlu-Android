package com.octoblu.beaconblu;

import android.util.Log;

/**
 * Created by octoblu on 8/3/15.
 */
public class BeaconInfo {
    private static final String TAG = "BeaconInfo";
    public String uuid;
    public Boolean status;
    public String name;
    public Integer sensitivity = 50;
    public Double lastDistance = 0.0;
    public Double sensitivityDistance = 2.0;
    private final Double MAX_SENSITIVITY = 4.0;

    public BeaconInfo(SaneJSONObject jsonObject){
        loadFromJSON(jsonObject);
        calculateSensitivity();
    }

    public void loadFromJSON(SaneJSONObject jsonObject){
        String newName = jsonObject.getStringOrNull("name");
        if(newName == null || newName.length() == 0){
            name = "Unknown Name";
        }else{
            name = newName;
        }
        String newUuid = jsonObject.getStringOrNull("uuid");
        if(newUuid == null || newUuid.length() == 0){
            uuid = "Unknown UUID";
        }else{
            uuid = newUuid;
        }
        status = jsonObject.getBoolean("status", false);
        Integer newSensitivity = jsonObject.getInteger("sensitivity", -1);
        if(newSensitivity >= 0){
            sensitivity = newSensitivity;
        }
    }

    public void setLastDistance(Double lastDistance){
        this.lastDistance = lastDistance;
    }

    private void calculateSensitivity(){
        sensitivityDistance = ((sensitivity / 100.0) * MAX_SENSITIVITY) % MAX_SENSITIVITY;
    }

    public Boolean hasChangedDistance(Double distance){
        calculateSensitivity();
        if(distance > (lastDistance + sensitivityDistance)){
            return true;
        }else if(distance < (lastDistance - sensitivityDistance)){
            return true;
        }
        return false;
    }

    public SaneJSONObject toJSON(){
        SaneJSONObject jsonObject = new SaneJSONObject().fromString("{}");
        jsonObject.putBooleanOrIgnore("status", status);
        jsonObject.putOrIgnore("name", name);
        jsonObject.putOrIgnore("uuid", uuid);
        jsonObject.putIntOrIgnore("sensitivity", sensitivity);
        return jsonObject;
    }
}

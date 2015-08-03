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
    public Double sensitivity = 2.0;
    public Double lastDistance = 0.0;

    public BeaconInfo(String name, String uuid, Boolean status){
        this.name = name;
        this.uuid = uuid;
        this.status = status;
    }

    public BeaconInfo(SaneJSONObject jsonObject){
        this.name = jsonObject.getStringOrNull("name");
        this.uuid = jsonObject.getStringOrNull("uuid");
        this.status = jsonObject.getBoolean("status", false);
    }

    public void setLastDistance(Double lastDistance){
        this.lastDistance = lastDistance;
    }

    public Boolean hasChangedDistance(Double distance){
        if(distance > (lastDistance + sensitivity)){
            return true;
        }else if(distance < (lastDistance - sensitivity)){
            return true;
        }
        return false;
    }

    public SaneJSONObject toJSON(){
        SaneJSONObject jsonObject = new SaneJSONObject().fromString("{}");
        jsonObject.putBooleanOrIgnore("status", status);
        jsonObject.putOrIgnore("name", name);
        jsonObject.putOrIgnore("uuid", uuid);
        return jsonObject;
    }
}

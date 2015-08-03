package com.octoblu.beaconblu;

/**
 * Created by octoblu on 8/3/15.
 */
public class BeaconInfo {
    public String uuid;
    public Boolean status;
    public String name;

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
}

package com.octoblu.beaconblu;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.MissingResourceException;

public class SaneJSONObject extends JSONObject {
    public SaneJSONObject() {
        super();
    }

    public SaneJSONObject(String jsonData) throws JSONException {
        super(jsonData);
    }

    public String getStringOrNull(@Nullable String name) {
        try {
            return this.getString(name);
        } catch (JSONException e) {
            return null;
        }
    }

    public Integer getInteger(@Nullable String name, Integer value) {
        try {
            return this.getInt(name);
        } catch (JSONException e) {
            return value;
        }
    }

    public Boolean getBoolean(@Nullable String name, Boolean def) {
        try {
            return this.getBoolean(name);
        } catch (JSONException e) {
            return def;
        }
    }

    public String getStringOrThrow(@Nullable String name) {
        String value = getStringOrNull(name);
        if (value == null) {
            throw new MissingResourceException("Missing " + name, "WebViewDevice", name);
        }
        return value;
    }

    public SaneJSONObject getJSONOrNull(@Nullable String name) {
        try {
            return new SaneJSONObject().fromJSONObject(this.getJSONObject(name));
        } catch (JSONException e) {
            return null;
        }
    }

    public void putOrIgnore(@Nullable String name, @Nullable String value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public void putBooleanOrIgnore(@Nullable String name, Boolean value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public void putIntOrIgnore(@Nullable String name, @Nullable Integer value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public void putDoubleOrIgnore(@Nullable String name, @Nullable Double value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public void putJSONOrIgnore(@Nullable String name, @Nullable JSONObject value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public void putArrayOrIgnore(@Nullable String name, @Nullable JSONArray value){
        try {
            put(name, value);
        } catch (JSONException e) {
            return;
        }
    }

    public static SaneJSONObject fromJSONObject(JSONObject data) {
        return fromString(data.toString());
    }

    public static SaneJSONObject fromString(String data) {
        try {
            return new SaneJSONObject(data);
        } catch (JSONException e) {
            return new SaneJSONObject();
        }
    }
}

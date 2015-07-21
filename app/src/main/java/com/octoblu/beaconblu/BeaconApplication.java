package com.octoblu.beaconblu;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.RegionBootstrap;
import org.altbeacon.beacon.startup.BootstrapNotifier;


public class BeaconApplication extends Application implements BootstrapNotifier {
    private static final String TAG = "BeaconApplication";
    private RegionBootstrap regionBootstrap;
    private BackgroundPowerSaver backgroundPowerSaver;
    private boolean haveDetectedBeaconsSinceBoot = false;
    private MonitoringActivity monitoringActivity = null;


    public void onCreate() {
        super.onCreate();
        BeaconManager beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(getParser("Estimote"));

        Log.d(TAG, "Starting Beacon, with");

        Region region = new Region("backgroundRegion", null, null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        backgroundPowerSaver = new BackgroundPowerSaver(this);
    }

    private BeaconParser getParser(String parser){
        String layout;
        switch(parser){
        case "Estimote":
            layout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
            break;
        case "iBeacon":
            layout = "m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24";
            break;
        case "easiBeacons":
            layout = "m:0-3=a7ae2eb7,i:4-19,i:20-21,i:22-23,p:24-24";
            break;
        default:
            layout = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
            break;
        }
        return new BeaconParser().setBeaconLayout(layout);
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "Did enter region.");
        if (!haveDetectedBeaconsSinceBoot) {
            Log.d(TAG, "Auto launching MontitoringActivity");

            Intent intent = new Intent(this, MonitoringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
            haveDetectedBeaconsSinceBoot = true;
        } else {
            if (monitoringActivity != null) {
                monitoringActivity.logToDisplay("I see a beacon again");
            } else {
                Log.d(TAG, "Sending notification...");
                sendNotification();
            }
        }
    }

    @Override
    public void didExitRegion(Region region) {
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I no longer see a beacon.");
        }
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        if (monitoringActivity != null) {
            monitoringActivity.logToDisplay("I have just switched from seeing/not seeing beacons: " + state);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("BeaconBlu Application")
                .setContentText("An beacon is nearby.");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, MonitoringActivity.class));
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

    public void setMonitoringActivity(MonitoringActivity activity) {
        this.monitoringActivity = activity;
    }
}


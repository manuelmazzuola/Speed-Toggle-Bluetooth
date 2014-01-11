package com.manuelmazzuola.speedtogglebluetooth.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.manuelmazzuola.speedtogglebluetooth.MainActivity;
import com.manuelmazzuola.speedtogglebluetooth.R;

/**
 * @author Manuel Mazzuola
 */
public class MonitorSpeed extends Service implements LocationListener {
    private int serviceReturnFlag = START_NOT_STICKY;
    private final String DEVICES = "devices";
    private final int intentId = 1988;
    private final float triggerDistance = 260f;
    private Location oldLocation;
    private LocationManager lm;
    private String provider;
    private static Boolean activated = Boolean.FALSE;
    private static Boolean running = Boolean.FALSE;

    @Override
    public void onLocationChanged(Location location) {
        float distance = oldLocation.distanceTo(location);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (distance >= triggerDistance && !activated) {
            bluetoothAdapter.enable();
            activated = true;
        }
        oldLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onCreate() {
        activated = BluetoothAdapter.getDefaultAdapter().isEnabled();
        running = Boolean.FALSE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!running) {
            running = Boolean.TRUE;

            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            provider = lm.NETWORK_PROVIDER;
            oldLocation = lm.getLastKnownLocation(provider);

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

            registerReceiver(bluetoothDeviceDisconnected, filter);
            lm.requestLocationUpdates(provider, 45 * 1000, 0f, this);

            Notification note = new Notification(
                    R.drawable.ic_action_bluetooth,
                    "Starting Speed Toggle bluetooth...",
                    System.currentTimeMillis());
            Intent noteIntent = new Intent(this, MainActivity.class);
            noteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pi = PendingIntent.getActivity(this, 0, noteIntent, 0);
            note.setLatestEventInfo(this,
                    "Speed Toggle Bluetooth",
                    "Service is running",
                    pi);
            note.flags |= Notification.FLAG_NO_CLEAR;

            startForeground(intentId, note);
        }

        return serviceReturnFlag;
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(this);
        unregisterReceiver(bluetoothDeviceDisconnected);

        running = Boolean.FALSE;
        stopForeground(true);

        super.onDestroy();
    }

    public static boolean getRunning() {
        return running.booleanValue();
    }

    private final BroadcastReceiver bluetoothDeviceDisconnected = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activated && intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = "";
                if (device != null) {
                    deviceAddress = device.getAddress();
                }

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                if (pref != null && pref.getString(DEVICES, "").equals(deviceAddress)) {
                    bluetoothAdapter.disable();

                    oldLocation = lm.getLastKnownLocation(provider);
                    activated = false;
                }
            }else {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                activated = state == BluetoothAdapter.STATE_ON;
            }
        }

    };
}
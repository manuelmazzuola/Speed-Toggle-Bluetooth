/**
 * Speed Toggle Bluetooth
 * Copyright (C) 2014  Manuel Mazzuola
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.manuelmazzuola.speedtogglebluetooth.MainActivity;
import com.manuelmazzuola.speedtogglebluetooth.R;

/**
 * @author Manuel Mazzuola
 */
public class MonitorSpeed extends Service implements LocationListener {
    private final int intentId = 1988;
    private final float triggerDistance = 260f;
    private Location oldLocation;
    private LocationManager lm;
    private String provider;
    private CountDownTimer countDownTimer;
    private String DEFAULT_TITLE;
    private String DEFAULT_MESSAGE;
    private String WAITING_MESSAGE;
    private String WAITING_TO_SLOWDOWN;
    private int counter;
    private static boolean activated = false;
    private static boolean disabled = false;
    private static boolean running = false;

    @Override
    public void onLocationChanged(Location location) {
        float distance = (oldLocation.distanceTo(location) - location.getAccuracy());
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(location.getAccuracy() > 0.0f)
            if (distance > triggerDistance && !activated && !disabled) {
                bluetoothAdapter.enable();
                activated = true;
                changeMessage(WAITING_MESSAGE);
                starTimer();
            }else if (disabled && distance < triggerDistance) {
                if(++counter > 1) {
                    disabled = false;
                    changeMessage(DEFAULT_MESSAGE);
                }
            }

        oldLocation = location;
    }

    @Override
    public void onCreate() {
        DEFAULT_TITLE  = this.getString(R.string.app_name);
        DEFAULT_MESSAGE = this.getString(R.string.service_is_running);
        WAITING_MESSAGE = this.getString(R.string.waiting_device);
        WAITING_TO_SLOWDOWN = this.getString(R.string.waiting_to_slowdown);
        activated = BluetoothAdapter.getDefaultAdapter().isEnabled();
        running = Boolean.FALSE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        running = Boolean.TRUE;

        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        provider = lm.NETWORK_PROVIDER;
        oldLocation = lm.getLastKnownLocation(provider);

        IntentFilter filters = new IntentFilter();
        // When to turn off bluetooth
        filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        // When hold bluetooth on
        filters.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        // When user directly turn on or off bluetooth
        filters.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(bluetoothListener, filters);
        lm.requestLocationUpdates(provider, 45 * 1000, 0f, this);

        Intent stopIntent = new Intent(this, MainActivity.class);
        stopIntent.putExtra("close", "close");
        PendingIntent stopPendingIntent =
                PendingIntent.getActivity(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder note =
                new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle(DEFAULT_TITLE)
                        .setContentText(DEFAULT_MESSAGE)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(stopPendingIntent)
                        .setSmallIcon(R.drawable.ic_action_bluetooth)
                ;

        note.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        Notification notification = note.build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        startForeground(intentId, note.build());

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        lm.removeUpdates(this);
        unregisterReceiver(bluetoothListener);

        running = Boolean.FALSE;
        stopForeground(true);

        super.onDestroy();
    }

    public static boolean getRunning() {
        return running;
    }

    private void starTimer() {
        countDownTimer = new CountDownTimer(30000, 30000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                changeMessage(DEFAULT_MESSAGE);
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.disable();
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void changeMessage(String message) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent stopIntent = new Intent(this, MainActivity.class);
        stopIntent.putExtra("close", "close");
        PendingIntent stopPendingIntent =
                PendingIntent.getActivity(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder note =
                new NotificationCompat.Builder(this)
                        .setContentTitle(DEFAULT_TITLE)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(stopPendingIntent)
                        .setSmallIcon(R.drawable.ic_action_bluetooth);

        note.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

        Notification notification = note.build();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(intentId, notification);
    }

    private final BroadcastReceiver bluetoothListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activated && intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                activated = false;
                disabled = false;
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                bluetoothAdapter.disable();
            } else if (activated && intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                disabled = false;
                stopTimer();
                changeMessage(DEFAULT_MESSAGE);
            }else if (activated && intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                counter = 0;
                disabled = true;
                activated = false;
                changeMessage(WAITING_TO_SLOWDOWN);
            }

            oldLocation = lm.getLastKnownLocation(provider);
        }
    };

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
}
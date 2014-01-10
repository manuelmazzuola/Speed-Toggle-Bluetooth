package com.manuelmazzuola.speedtogglebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.manuelmazzuola.speedtogglebluetooth.fragment.MainFragment;
import com.manuelmazzuola.speedtogglebluetooth.service.MonitorSpeed;

/**
 * @author Manuel Mazzuola
 */
public class MainActivity extends FragmentActivity {
    private static Boolean bluetoothWasEnabled;
    private final String DEVICES = "devices";
    private Context ctx;
    private final BluetoothAdapter bluetoothAdapter =
            BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) { // Settings activity
            bluetoothWasEnabled = bluetoothAdapter.isEnabled();
            if(!bluetoothWasEnabled) {
                registerForBluetoothChanges();
                bluetoothAdapter.enable();
            }else {
                startSettingsActivity();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        ctx = getApplicationContext();
        if(bluetoothWasEnabled != null && !bluetoothWasEnabled) {
            bluetoothAdapter.disable();
        }
    }

    private void registerForBluetoothChanges() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(callStartSettingsActivityWhenBluetoothIsEnabled, filter);
    }

    private void startSettingsActivity() {
        Intent settingsActivity = new Intent(ctx, SettingsActivity.class);
        startActivity(settingsActivity);
    }

    public void onToggleButton(View view) {
        CompoundButton buttonView = (CompoundButton) view;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);

        if(pref != null && pref.getString(DEVICES, "").equals("")) {
            buttonView.toggle();
            Utils.showAlert(this,
                    "Please, select a device from settings page before start the service.",
                    "Device missing");
        }else if (buttonView.isChecked()) {
            startService(new Intent(ctx, MonitorSpeed.class));
        } else {
            stopService(new Intent(ctx, MonitorSpeed.class));
        }
    }

    private final BroadcastReceiver
        callStartSettingsActivityWhenBluetoothIsEnabled = new BroadcastReceiver() {

    @Override
    public void onReceive(Context context, Intent intent) {
        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                BluetoothAdapter.ERROR);

        if (state == BluetoothAdapter.STATE_ON) {
            unregisterReceiver(callStartSettingsActivityWhenBluetoothIsEnabled);
            startSettingsActivity();
        }
        }
    };
}

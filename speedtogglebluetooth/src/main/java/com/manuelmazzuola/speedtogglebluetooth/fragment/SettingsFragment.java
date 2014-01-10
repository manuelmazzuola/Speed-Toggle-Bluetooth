package com.manuelmazzuola.speedtogglebluetooth.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.ListPreference;
import android.support.v4.preference.PreferenceFragment;

import com.manuelmazzuola.speedtogglebluetooth.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Manuel Mazzuola
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        final ListPreference listPreference = (ListPreference) findPreference("devices");

        if (listPreference != null) {

            BluetoothAdapter bluetoothAdapter
                    = BluetoothAdapter.getDefaultAdapter();
            // FIXME: If bluetoot is disabled getBondedDevice return {}
            Set<BluetoothDevice> pairedDevices
                    = bluetoothAdapter.getBondedDevices();

            // Getting paired devices
            if (pairedDevices != null) {
                List<String> listPairedDevices = new ArrayList<String>();
                List<String> listPairedDevicesHash = new ArrayList<String>();

                for (BluetoothDevice device : pairedDevices) {
                    listPairedDevices.add(device.getName());
                    listPairedDevicesHash.add(device.getAddress());
                }

                final CharSequence[] entries =
                        listPairedDevices.toArray(new CharSequence[listPairedDevices.size()]);
                final CharSequence[] entryValues =
                        listPairedDevicesHash.toArray(new CharSequence[listPairedDevicesHash.size()]);

                listPreference.setEntries(entries);
                listPreference.setEntryValues(entryValues);
            }
        }
    }
}
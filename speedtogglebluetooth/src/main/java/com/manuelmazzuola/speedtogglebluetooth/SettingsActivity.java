package com.manuelmazzuola.speedtogglebluetooth;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.manuelmazzuola.speedtogglebluetooth.fragment.SettingsFragment;

/**
 * @author Manuel Mazzuola
 */
public class SettingsActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display settings fragment as the main content.
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
package com.manuelmazzuola.speedtogglebluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.manuelmazzuola.speedtogglebluetooth.service.MonitorSpeed;

/**
 * @author Manuel Mazzuola
 */
public class MainActivity extends Activity {
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstance) {

        Bundle extras;
        String haveToStop = "";
        if (savedInstance == null) {
            extras = getIntent().getExtras();
            if (extras != null)
                haveToStop = extras.getString("close");

        } else {
            haveToStop = (String) savedInstance.getSerializable("close");
        }

        if(haveToStop != null && haveToStop.equals("close")) {
            stopService(new Intent(getApplicationContext(), MonitorSpeed.class));
            finish();
        }

        super.onCreate(savedInstance);
        ctx = this.getApplicationContext();
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean isServiceRunning = MonitorSpeed.getRunning();

        if (!isServiceRunning) {
            startService(new Intent(ctx, MonitorSpeed.class));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        finish();
    }
}

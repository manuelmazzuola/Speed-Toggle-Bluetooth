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

package com.manuelmazzuola.speedtogglebluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.manuelmazzuola.speedtogglebluetooth.service.MonitorSpeed;

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

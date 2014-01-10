package com.manuelmazzuola.speedtogglebluetooth.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.manuelmazzuola.speedtogglebluetooth.R;
import com.manuelmazzuola.speedtogglebluetooth.service.MonitorSpeed;

/**
 * @author Manuel Mazzuola
 */
public class MainFragment extends Fragment {
    public MainFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ToggleButton toggleButton = (ToggleButton) rootView.findViewById(R.id.togglebutton);
        toggleButton.setChecked(MonitorSpeed.getRunning());

        return rootView;
    }
}
package com.manuelmazzuola.speedtogglebluetooth.test;

import android.widget.ToggleButton;

import com.manuelmazzuola.speedtogglebluetooth.MainActivity;
import com.manuelmazzuola.speedtogglebluetooth.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Robolectric;

import org.junit.Test;
import org.junit.Before;
import org.junit.runner.RunWith;

import static org.fest.assertions.api.ANDROID.assertThat;

/**
 * @author Manuel Mazzuola
 */
@RunWith(RobolectricGradleTestRunner.class)
public class MainActivityTest {
    private MainActivity activity;

    @Before
    public void setup() {
        activity = Robolectric.buildActivity(MainActivity.class).get();
    }

    @Test
    public void shouldNotBeNull() {
        assertThat(activity).isNotNull();
    }
}
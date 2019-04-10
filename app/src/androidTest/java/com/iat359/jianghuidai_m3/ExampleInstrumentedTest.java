package com.iat359.jianghuidai_m3;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented toolbar_calendar_background, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under toolbar_calendar_background.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.iat359.jianghuidai_m2", appContext.getPackageName());
    }
}

package org.tlc.whereat.fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;

import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.FragmentTestUtil.startFragment;
import static org.assertj.core.api.Assertions.*;
import static org.robolectric.util.FragmentTestUtil.startVisibleFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)


public class SettingsFragmentTest {

    static SettingsFragment frag;
    static FakePreferenceActivity a;
    static String FRAG_TAG = "settings_fragment";

    private static class FakePreferenceActivity extends Activity {

        @Override
        protected void onCreate(Bundle state) {
          super.onCreate(state);
          getFragmentManager().beginTransaction().replace(
              android.R.id.content,
              new SettingsFragment(),
              FRAG_TAG
          ).commit();
        }
  }

@Before
public void setup() {
    a = Robolectric.buildActivity(FakePreferenceActivity.class).create().start().resume().visible().get();
    frag = (SettingsFragment) a.getFragmentManager().findFragmentByTag(FRAG_TAG);
}

@Test
public void onCreate_should_createFragmentWithCorrectContents(){
    assertThat(frag).isNotNull();
}


}
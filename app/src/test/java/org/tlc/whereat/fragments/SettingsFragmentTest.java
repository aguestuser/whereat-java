package org.tlc.whereat.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.ListPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowPreferenceManager;
import org.tlc.whereat.BuildConfig;
import org.tlc.whereat.R;

import static org.robolectric.util.FragmentTestUtil.startFragment;
import static org.assertj.core.api.Assertions.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class SettingsFragmentTest {

    static SettingsFragment frag;
    static Context ctx = RuntimeEnvironment.application;
    static SharedPreferences prefs;
    static ListPreference lp;

    static CharSequence[] locShareVals = {
        ctx.getString(R.string.pref_loc_share_interval_value_0),
        ctx.getString(R.string.pref_loc_share_interval_value_1),
        ctx.getString(R.string.pref_loc_share_interval_value_2),
        ctx.getString(R.string.pref_loc_share_interval_value_3),
        ctx.getString(R.string.pref_loc_share_interval_value_4)
    };

    static CharSequence[] locShareLabels = {
        ctx.getString(R.string.pref_loc_share_interval_label_0),
        ctx.getString(R.string.pref_loc_share_interval_label_1),
        ctx.getString(R.string.pref_loc_share_interval_label_2),
        ctx.getString(R.string.pref_loc_share_interval_label_3),
        ctx.getString(R.string.pref_loc_share_interval_label_4)
    };
    
    static CharSequence[] locTtlVals = {
        ctx.getString(R.string.pref_loc_ttl_value_0),
        ctx.getString(R.string.pref_loc_ttl_value_1),
        ctx.getString(R.string.pref_loc_ttl_value_2),
    };

    static CharSequence[] locTtlLabels = {
        ctx.getString(R.string.pref_loc_ttl_label_0),
        ctx.getString(R.string.pref_loc_ttl_label_1),
        ctx.getString(R.string.pref_loc_ttl_label_2),
    };
    

    @Before
    public void setup() {
        frag = new SettingsFragment();
        startFragment(frag);
        prefs = ShadowPreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
    }

    @Test
    public void fragment_should_notBeNull(){
        assertThat(frag).isNotNull();
    }

    @Test
    public void locShareIntervalPref_should_haveCorrectDefaultContents(){
        lp = findListPref(R.string.pref_loc_share_interval_key);

        assertThat(lp).isNotNull();
        assertThat(lp.isPersistent()).isTrue();

        assertThat(lp.getTitle()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_title));
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_2));

        assertThat(lp.getEntries()).isEqualTo(locShareLabels);
        assertThat(lp.getEntryValues()).isEqualTo(locShareVals);
        assertThat(lp.getValue()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_value_2));
    }

    @Test
    public void locShareIntervalPref_should_updateSummaryWhenPrefChanges(){
        lp = findListPref(R.string.pref_loc_share_interval_key);

        setListPref(R.string.pref_loc_share_interval_value_0);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_0));

        setListPref(R.string.pref_loc_share_interval_value_1);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_1));

        setListPref(R.string.pref_loc_share_interval_value_2);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_2));

        setListPref(R.string.pref_loc_share_interval_value_3);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_3));

        setListPref(R.string.pref_loc_share_interval_value_4);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_share_interval_label_4));
    }

    @Test
    public void locTtlPref_should_haveCorrectDefaultContents(){
        lp = findListPref(R.string.pref_loc_ttl_key);

        assertThat(lp).isNotNull();
        assertThat(lp.isPersistent()).isTrue();

        assertThat(lp.getTitle()).isEqualTo(frag.getString(R.string.pref_loc_ttl_title));
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_ttl_label_1));

        assertThat(lp.getEntries()).isEqualTo(locTtlLabels);
        assertThat(lp.getEntryValues()).isEqualTo(locTtlVals);

        assertThat(lp.getEntry()).isEqualTo(frag.getString(R.string.pref_loc_ttl_label_1));
        assertThat(lp.getValue()).isEqualTo(frag.getString(R.string.pref_loc_ttl_value_1));
    }

    @Test
    public void locTtlPref_should_updateSummaryWhenPrefChanges(){
        lp = findListPref(R.string.pref_loc_ttl_key);

        setListPref(R.string.pref_loc_ttl_value_0);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_ttl_label_0));

        setListPref(R.string.pref_loc_ttl_value_1);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_ttl_label_1));

        setListPref(R.string.pref_loc_ttl_value_2);
        assertThat(lp.getSummary()).isEqualTo(frag.getString(R.string.pref_loc_ttl_label_2));
    }

    protected ListPreference findListPref(int id){
        return (ListPreference) frag.findPreference(frag.getString(id));
    }

    protected void setListPref(int valId){
        lp.setValue(frag.getString(valId));
    }

}
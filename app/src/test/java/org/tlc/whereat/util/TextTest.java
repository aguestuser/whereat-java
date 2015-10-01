package org.tlc.whereat.util;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.URLSpan;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.tlc.whereat.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)

public class TextTest {

    @Test
    public void linkify_should_addALinkToASubstring(){
        SpannableString res =
            Text.linkify("Please visit our homepage.","our homepage.", "https://", "whereat.io");
        URLSpan[] spans = res.getSpans(0, res.length(), URLSpan.class);
        String link = spans[0].getURL();

        assertThat(res.toString()).isEqualTo("Please visit our homepage.");
        assertThat(spans.length).isEqualTo(1);
        assertThat(link).isEqualTo("https://whereat.io");
    }

}
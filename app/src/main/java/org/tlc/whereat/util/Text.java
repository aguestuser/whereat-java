package org.tlc.whereat.util;

import android.text.SpannableString;
import android.text.util.Linkify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class Text {

    public static SpannableString linkify(String txt, String linkTxt, String scheme, String url) {
        SpannableString s = new SpannableString(txt);
        boolean yup = Linkify.addLinks(
            s, Pattern.compile(linkTxt), scheme,
            new Linkify.MatchFilter(){
                @Override
                public boolean acceptMatch(CharSequence c, int s, int e){
                    return true;
                }
            },
            new Linkify.TransformFilter(){
                @Override
                public String transformUrl(Matcher match, String baseUrl) {
                    return url;
                }
            }
        );
        if(yup) return s;
        else return null;
    }

}

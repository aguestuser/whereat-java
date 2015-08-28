package org.tlc.whereat.support;

import android.content.Context;

import java.lang.reflect.Field;

/**
 * Author: @aguestuser
 * License: GPLv3 (https://www.gnu.org/licenses/gpl-3.0.html)
 */
public class ReflectionHelpers {

    public static <T extends Class> Field publicify(T klass, String fieldName){
        Field field;
        try {
            field = klass.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> void override (Class<T> klass, String fieldName, Object val){
        try {
            publicify(klass, fieldName).set(klass.getClass(), val);
        } catch (Exception e) {
            return;
        }
    }


}

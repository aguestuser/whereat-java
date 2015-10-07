package org.tlc.whereat.util;

import android.location.Location;

import java.util.List;

public class CollectionUtils {

    public static <T> T last(List<T> list){
        return list.get(list.size() -1);
    }

}

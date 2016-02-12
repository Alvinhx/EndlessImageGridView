package com.alvinhx.endlessImageGridView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * To store photo data
 *
 * Created by solor on 2016-02-10.
 */
public class Data {
    public static volatile int offset = 1;
    public static volatile ArrayList<HashMap<String,Object>> photos_map = new ArrayList<HashMap<String, Object>>();
}

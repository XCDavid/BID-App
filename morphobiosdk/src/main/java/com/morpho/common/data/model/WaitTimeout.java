package com.morpho.common.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alfredohernandez on 06/06/17.
 * Wait Timeout
 */

public enum WaitTimeout {
    WAIT_30(30),
    WAIT_60(60),
    WAIT_90(90),
    WAIT_120(120),
    WAIT_150(150);



    private static final Map<Integer, String> int2enum = new HashMap<Integer, String>();

    static {
        for (WaitTimeout time : values()) {
            int2enum.put(time.getIntegerValue(), time.name());
        }
    }

    private int value;

    WaitTimeout(int value) {
        this.value = value;
    }

    public int getIntegerValue() {
        return value;
    }

    public static String getEnum(int key){
        return int2enum.get(key);
    }
}

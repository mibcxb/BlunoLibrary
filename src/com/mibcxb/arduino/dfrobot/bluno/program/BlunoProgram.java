package com.mibcxb.arduino.dfrobot.bluno.program;

import android.text.TextUtils;

public enum BlunoProgram {
    TANK, UNKNOWN;

    public static BlunoProgram fromName(String name) {
        if (TextUtils.equals(name, TANK.name())) {
            return TANK;
        } else {
            return UNKNOWN;
        }
    }
}

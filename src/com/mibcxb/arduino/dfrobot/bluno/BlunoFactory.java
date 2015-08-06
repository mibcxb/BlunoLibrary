package com.mibcxb.arduino.dfrobot.bluno;

import com.mibcxb.arduino.dfrobot.bluno.program.BlunoProgram;
import com.mibcxb.arduino.dfrobot.bluno.program.Tank;

public class BlunoFactory {
    public static Bluno create(BlunoBase base, BlunoProgram program) {
        Bluno bluno;
        switch (program) {
        case TANK:
            bluno = new Tank(base);
            break;
        default:
            bluno = base;
            break;
        }
        return bluno;
    }
}

package com.mibcxb.arduino.dfrobot.bluno;

import android.bluetooth.BluetoothDevice;

import com.mibcxb.arduino.dfrobot.bluno.program.BlunoProgram;
import com.mibcxb.arduino.dfrobot.bluno.program.Tank;

public class BlunoFactory {
    public static Bluno create(BluetoothDevice device, BlunoProgram program) {
        Bluno bluno;
        switch (program) {
        case TANK:
            bluno = new Tank(device);
            break;
        default:
            bluno = new BlunoBase(device);
            break;
        }
        return bluno;
    }
}

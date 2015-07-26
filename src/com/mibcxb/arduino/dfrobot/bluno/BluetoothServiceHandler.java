package com.mibcxb.arduino.dfrobot.bluno;

import android.os.Message;

import com.mibcxb.android.os.McHandler;

public class BluetoothServiceHandler extends McHandler<BluetoothService> {

    public BluetoothServiceHandler(BluetoothService r) {
        super(r);
    }

    @Override
    public void handleMessage(BluetoothService r, Message msg) {
    }

}

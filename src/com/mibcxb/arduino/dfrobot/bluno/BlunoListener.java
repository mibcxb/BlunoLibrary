package com.mibcxb.arduino.dfrobot.bluno;

public interface BlunoListener {
    void onStateChanged();

    void onReadSerial(byte[] data);
}

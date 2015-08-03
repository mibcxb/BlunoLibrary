package com.mibcxb.arduino.dfrobot.bluno;

import android.content.Context;

public interface Bluno {
    public void connect(Context context);

    public void disconnect();

    public void reconnect(Context context);

    public BlunoState getState();

    public boolean isReady();

    public void writeSerial(byte[] data);
}

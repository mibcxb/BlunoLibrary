package com.mibcxb.arduino.dfrobot.bluno.program;

import java.util.Locale;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.mibcxb.arduino.dfrobot.bluno.Bluno;
import com.mibcxb.arduino.dfrobot.bluno.BlunoBase;
import com.mibcxb.arduino.dfrobot.bluno.BlunoState;

public class Tank implements Bluno {
    public static final String CMD_MOVE = "BLC+MOVE=%s";
    public static final String CMD_SPEED = "BLC+SPEED=%d";

    public static final String MOVE_FORWARD = "MF";
    public static final String MOVE_BACKWARD = "MB";
    public static final String TURN_LEFT = "TL";
    public static final String TURN_RIGHT = "TR";

    public static final int SPEED_STOP = 0;
    public static final int SPEED_BASE = 155;
    public static final int SPEED_MIN = 1;
    public static final int SPEED_MAX = 100;

    private final BlunoBase mBlunoBase;

    public Tank(BluetoothDevice device) {
        this(new BlunoBase(device));
    }

    public Tank(BlunoBase blunoBase) {
        this.mBlunoBase = blunoBase;
        this.mBlunoBase.setProgram(BlunoProgram.TANK);
    }

    public BlunoBase getBlunoBase() {
        return mBlunoBase;
    }

    private String getCommand(String cmd, Object obj) {
        return String.format(Locale.US, cmd, obj);
    }

    public void stop() {
        mBlunoBase.writeSerial(getCommand(CMD_SPEED, SPEED_STOP));
    }

    public void move() {
        mBlunoBase.writeSerial(getCommand(CMD_SPEED, MOVE_FORWARD));
    }

    public void speed(int value) {
        mBlunoBase.writeSerial(getCommand(CMD_SPEED,
                SPEED_BASE + Math.min(SPEED_MAX, Math.max(SPEED_MIN, value))));
    }

    public void forward() {
        mBlunoBase.writeSerial(getCommand(CMD_MOVE, MOVE_FORWARD));
    }

    public void backward() {
        mBlunoBase.writeSerial(getCommand(CMD_MOVE, MOVE_BACKWARD));
    }

    public void left() {
        mBlunoBase.writeSerial(getCommand(CMD_MOVE, TURN_LEFT));
    }

    public void right() {
        mBlunoBase.writeSerial(getCommand(CMD_MOVE, TURN_RIGHT));
    }

    @Override
    public void connect(Context context) {
        mBlunoBase.connect(context);
    }

    @Override
    public void disconnect() {
        mBlunoBase.disconnect();
    }

    @Override
    public void reconnect(Context context) {
        mBlunoBase.reconnect(context);
    }

    @Override
    public BlunoState getState() {
        return mBlunoBase.getState();
    }

    @Override
    public boolean isReady() {
        return mBlunoBase.isReady();
    }

    @Override
    public void writeSerial(byte[] data) {
        mBlunoBase.writeSerial(data);
    }

    public void writeSerial(String str) {
        mBlunoBase.writeSerial(str);
    }
}

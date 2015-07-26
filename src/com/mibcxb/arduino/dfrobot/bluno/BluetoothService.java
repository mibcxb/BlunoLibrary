package com.mibcxb.arduino.dfrobot.bluno;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class BluetoothService extends Service {
    private static final String TAG = BluetoothService.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 0x10000001;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private Handler mServiceHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isReady()) {
            initialize();
        }
        return START_STICKY;
    }

    private void initialize() {
        mServiceHandler = new BluetoothServiceHandler(this);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (!mBluetoothAdapter.isEnabled()) {
            enableBluetooth();
        }
    }

    private boolean isReady() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    private void enableBluetooth() {
        if (mBluetoothAdapter != null) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
        }
    }

}

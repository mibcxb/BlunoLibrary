package com.mibcxb.arduino.dfrobot.bluno;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.mibcxb.arduino.dfrobot.bluno.program.Tank;

public class BlunoManager {
    private static final String TAG = BlunoManager.class.getSimpleName();

    private static final long SCAN_PERIOD = 30 * 1000;

    private static final int WHAT_SCAN_START = 0xf0000001;
    private static final int WHAT_SCAN_STOP = 0xf0000002;

    private static BlunoManager sInstance;

    private Context mContext;
    private Handler mHandler;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning = false;
    private BlunoScanListener mOnChangeListener;

    private Map<String, Bluno> mDeviceMap = new HashMap<String, Bluno>();

    private BlunoManager() {
    }

    public static BlunoManager getInstance() {
        if (sInstance == null) {
            sInstance = new BlunoManager();
        }
        return sInstance;
    }

    public synchronized void initialize(Context context) {
        mContext = context;
        mHandler = new BlunoManagerHanlder(this);

        mBluetoothManager = (BluetoothManager) context
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    public synchronized void release() {
        for (Bluno bluno : mDeviceMap.values()) {
            bluno.disconnect();
        }
        mDeviceMap.clear();
        mContext = null;
        mHandler = null;
        mBluetoothManager = null;
        mBluetoothAdapter = null;
    }

    public synchronized boolean isReady() {
        return mBluetoothAdapter != null && mBluetoothAdapter.isEnabled();
    }

    public synchronized List<Bluno> getBlunoList() {
        List<Bluno> list = new ArrayList<Bluno>();
        for (Bluno bluno : mDeviceMap.values()) {
            list.add(bluno);
        }
        return list;
    }

    public synchronized Bluno getBlunoByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            Log.d(TAG, "Invalid MAC address = " + address);
        }
        return mDeviceMap.get(address);
    }

    public synchronized void scanLeDevice(boolean enable) {
        if (enable) {
            if (!mScanning) {
                mScanning = true;
                mHandler.sendEmptyMessageDelayed(WHAT_SCAN_STOP, SCAN_PERIOD);
                mHandler.sendEmptyMessage(WHAT_SCAN_START);
            }
        } else {
            mScanning = false;
            mHandler.removeMessages(WHAT_SCAN_STOP);
            mHandler.sendEmptyMessage(WHAT_SCAN_STOP);
        }
    }

    private LeScanCallback mLeScanCallback = new LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            String name = device.getName();
            String address = device.getAddress();
            if (TextUtils.isEmpty(name)
                    || !name.contains(Bluno.class.getSimpleName())) {
                return;
            }

            Bluno bluno = mDeviceMap.get(address);
            if (bluno == null) {
                bluno = new Tank(device);
                bluno.connect(mContext);
                mDeviceMap.put(address, bluno);
                if (mOnChangeListener != null) {
                    mOnChangeListener.onScan(bluno);
                }
            }
        }
    };

    public BlunoScanListener getOnChangeListener() {
        return mOnChangeListener;
    }

    public void setOnChangeListener(BlunoScanListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    static class BlunoManagerHanlder extends Handler {
        private final WeakReference<BlunoManager> reference;

        public BlunoManagerHanlder(BlunoManager manager) {
            this.reference = new WeakReference<BlunoManager>(manager);
        }

        @Override
        public void handleMessage(Message msg) {
            BlunoManager manager = reference.get();
            if (manager != null) {
                handleMessage(manager, msg);
            }
        }

        @SuppressWarnings("deprecation")
        public void handleMessage(BlunoManager r, Message msg) {
            switch (msg.what) {
            case WHAT_SCAN_START:
                if (r.isReady()) {
                    r.mBluetoothAdapter.startLeScan(r.mLeScanCallback);
                }
                break;
            case WHAT_SCAN_STOP:
                if (r.isReady()) {
                    r.mBluetoothAdapter.stopLeScan(r.mLeScanCallback);
                }
                break;
            default:
                break;
            }
        }
    }

    public interface BlunoScanListener {
        void onScan(Bluno bluno);
    }
}

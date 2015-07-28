package com.mibcxb.arduino.dfrobot.bluno;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

public class Bluno {
    private static final String TAG = Bluno.class.getSimpleName();

    public static final Charset ISO_8859_1 = Charset.forName("iso-8859-1");
    public static final String EMPTY = "";

    public static final String UUID_MODEL = "00002a24-0000-1000-8000-00805f9b34fb";
    public static final String UUID_SERIAL = "0000dfb1-0000-1000-8000-00805f9b34fb";
    public static final String UUID_COMMAND = "0000dfb2-0000-1000-8000-00805f9b34fb";

    private static final String CMD_PASSWORD = "AT+PASSWOR=DFRobot\r\n";
    private static final String CMD_BAUDRATE = "AT+CURRUART=%d\r\n";

    private int mBaudrate = 115200; // set the default baud rate to 115200

    private final BluetoothDevice mDevice;
    private BluetoothGatt mBluetoothGatt;
    private final Map<String, BluetoothGattCharacteristic> mCharacteristicMap;

    private BlunoState mState = BlunoState.DISCONNECTED;
    private BlunoListener mListener = null;
    private boolean mReady = false;

    public Bluno(BluetoothDevice device) {
        this.mDevice = device;
        this.mCharacteristicMap = new HashMap<String, BluetoothGattCharacteristic>();
    }

    public synchronized void connect(Context context) {
        mBluetoothGatt = mDevice.connectGatt(context, false, callback);
    }

    public synchronized void disconnect() {
        Log.d(TAG, "disconnect");
        mReady = false;
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public synchronized void reconnect(Context context) {
        disconnect();
        connect(context);
    }

    public int getBaudrate() {
        return mBaudrate;
    }

    public void setBaudrate(int baudrate) {
        this.mBaudrate = baudrate;
    }

    public BlunoState getState() {
        return mState;
    }

    public void setState(BlunoState state) {
        if (this.mState != state) {
            this.mState = state;
            if (mListener != null) {
                mListener.onStateChanged();
            }
        }
    }

    public BlunoListener getListener() {
        return mListener;
    }

    public void setListener(BlunoListener listener) {
        this.mListener = listener;
    }

    public synchronized boolean isReady() {
        return mState == BlunoState.CONNECTED && mReady;
    }

    public void writeSerial(String str) {
        writeSerial(encode(str));
    }

    public void writeSerial(byte[] data) {
        if (data != null) {
            BluetoothGattCharacteristic characteristic = mCharacteristicMap
                    .get(UUID_SERIAL);
            characteristic.setValue(data);
            mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    private BluetoothGattCallback callback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            switch (newState) {
            case BluetoothProfile.STATE_CONNECTED:
                setState(BlunoState.CONNECTED);
                if (gatt.discoverServices()) {
                    Log.d(TAG, "BluetoothGatt.discoverServices:success");
                } else {
                    Log.d(TAG, "BluetoothGatt.discoverServices:failure");
                }
                break;
            case BluetoothProfile.STATE_CONNECTING:
                setState(BlunoState.CONNECTING);
                break;
            case BluetoothProfile.STATE_DISCONNECTING:
                setState(BlunoState.DISCONNECTING);
                break;
            default:
                setState(BlunoState.DISCONNECTED);
                break;
            }
            Log.d(TAG, "Bluno current state = " + getState());
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            List<BluetoothGattService> services = gatt.getServices();
            for (BluetoothGattService service : services) {
                List<BluetoothGattCharacteristic> characteristics = service
                        .getCharacteristics();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    String uuid = characteristic.getUuid().toString();
                    if (TextUtils.equals(uuid, UUID_MODEL)) {
                        mCharacteristicMap.put(uuid, characteristic);
                        gatt.setCharacteristicNotification(characteristic, true);
                        gatt.readCharacteristic(characteristic);
                    } else if (TextUtils.equals(uuid, UUID_SERIAL)
                            || TextUtils.equals(uuid, UUID_COMMAND)) {
                        mCharacteristicMap.put(uuid, characteristic);
                    }
                }
            }
        };

        public void onCharacteristicChanged(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            String uuid = characteristic.getUuid().toString();
            Log.d(TAG, "onCharacteristicChanged uuid = " + uuid + ", value = "
                    + Arrays.toString(characteristic.getValue()));

            if (TextUtils.equals(uuid, UUID_SERIAL)) {
                byte[] data = characteristic.getValue();
                if (mListener != null) {
                    mListener.onReadSerial(data);
                }
            }
        };

        public void onCharacteristicRead(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            String uuid = characteristic.getUuid().toString();
            Log.d(TAG, "onCharacteristicRead uuid = " + uuid + ", value = "
                    + Arrays.toString(characteristic.getValue()));

            if (TextUtils.equals(uuid, UUID_MODEL)) {
                gatt.setCharacteristicNotification(characteristic, false);

                byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    String model = decode(data);
                    if (model.toUpperCase(Locale.US).startsWith("DF BLUNO")) {
                        BluetoothGattCharacteristic command = mCharacteristicMap
                                .get(UUID_COMMAND);
                        command.setValue(encode(CMD_PASSWORD));
                        gatt.writeCharacteristic(command);
                        command.setValue(encode(String.format(Locale.US,
                                CMD_BAUDRATE, mBaudrate)));
                        gatt.writeCharacteristic(command);

                        BluetoothGattCharacteristic serial = mCharacteristicMap
                                .get(UUID_SERIAL);
                        gatt.setCharacteristicNotification(serial, true);
                        mReady = true;
                    }
                }
            }
        };

        public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            String uuid = characteristic.getUuid().toString();
            Log.d(TAG, "onCharacteristicWrite uuid = " + uuid + ", value = "
                    + Arrays.toString(characteristic.getValue()));
        };
    };

    public static byte[] encode(String str) {
        if (str == null) {
            return new byte[0];
        }
        byte[] data = str.getBytes();
        return new String(data, ISO_8859_1).getBytes();
    }

    public static String decode(byte[] data) {
        return data == null ? EMPTY : new String(data, ISO_8859_1);
    }
}

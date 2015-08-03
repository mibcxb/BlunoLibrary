package com.mibcxb.arduino.dfrobot.bluno;

import java.util.Arrays;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mibcxb.arduino.dfrobot.bluno.BlunoManager.BlunoScanListener;
import com.mibcxb.arduino.dfrobot.bluno.program.Tank;

public class BlunoTestActivity extends Activity {
    private static final String TAG = BlunoTestActivity.class.getSimpleName();

    private BlunoManager mBlunoManager;

    private EditText etInput;
    private Button btnSend;

    private Tank mTank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluno_test);

        mBlunoManager = BlunoManager.getInstance();
        mBlunoManager.initialize(getApplicationContext());
        mBlunoManager.setOnChangeListener(mBlunoScanListener);

        mBlunoManager.scanLeDevice(true);

        etInput = (EditText) findViewById(R.id.et_input);

        btnSend = (Button) findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String content = etInput.getEditableText().toString();
                if (!TextUtils.isEmpty(content) && mTank.isReady()) {
                    mTank.writeSerial(content);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mBlunoManager.release();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.menu.bluno_test);
        return super.onCreateOptionsMenu(menu);
    }

    private BlunoScanListener mBlunoScanListener = new BlunoScanListener() {

        @Override
        public void onScan(Bluno bluno) {
            if (bluno instanceof Tank) {
                mTank = (Tank) bluno;
                mTank.getBlunoBase().setListener(mBlunoListener);
            }
        }
    };

    private BlunoListener mBlunoListener = new BlunoListener() {

        @Override
        public void onStateChanged() {
            Log.d(TAG, "onStateChanged: " + mTank.getState());
        }

        @Override
        public void onReadSerial(byte[] data) {
            Log.d(TAG, "onReadSerial: " + Arrays.toString(data));
        }
    };
}

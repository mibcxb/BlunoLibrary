/**
 * 
 */
package com.mibcxb.android.os;

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author mibcxb
 *
 */
public class McHandler<T> extends Handler {

    private final WeakReference<T> reference;

    public McHandler(T r) {
        this.reference = new WeakReference<T>(r);
    }

    public McHandler(T r, Callback callback) {
        super(callback);
        this.reference = new WeakReference<T>(r);
    }

    public McHandler(T r, Looper looper) {
        super(looper);
        this.reference = new WeakReference<T>(r);
    }

    public McHandler(T r, Looper looper, Callback callback) {
        super(looper, callback);
        this.reference = new WeakReference<T>(r);
    }

    @Override
    public final void handleMessage(Message msg) {
        T r = reference.get();
        if (r != null) {
            handleMessage(r, msg);
        }
    }

    public void handleMessage(T r, Message msg) {
    }

}

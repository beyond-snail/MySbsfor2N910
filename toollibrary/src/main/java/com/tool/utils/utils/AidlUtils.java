package com.tool.utils.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.system.AidlSystem;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class AidlUtils {

    private static final String TAG = "AidlUtils";

    private static AidlUtils aidlUtils;

    private static Context mContext;

    private AidlDeviceService aidlDeviceService = null;

    /**
     * 系统及设备信息
     */
    AidlSystem aidlSystem = null;

    private AidlUtils(Context context){
        this.mContext = context;
        context.bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);

    }

    public synchronized static AidlUtils getInstance(Context context) {
        if (aidlUtils == null) {
            aidlUtils = new AidlUtils(context);
        }
        return aidlUtils;
    }



    /**
     * 服务连接
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
        }
    };
}

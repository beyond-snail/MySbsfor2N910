package com.tool.utils.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.magcard.AidlMagCard;
import com.nld.cloudpos.aidl.magcard.MagCardListener;
import com.nld.cloudpos.aidl.magcard.TrackData;
import com.nld.cloudpos.aidl.system.AidlSystem;
import com.nld.cloudpos.aidl.system.ApnData;
import com.nld.cloudpos.data.AidlErrorCode;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class ToolNewLand {

    private static String TAG = "ToolNewLand";

    private Context mContext;

    private AidlDeviceService aidlDeviceService = null;

    /**
     * 磁条卡
     */
    private AidlMagCard aidlMagCard = null;

    /**
     * 系统及设备信息
     */
    AidlSystem aidlSystem = null;

    public final static int magcard = 0;
    public final static int print = 1;
    public final static int serialNo = 2;

    private int currentType = -1;
    DeviceListenser mlistener;

    public interface DeviceListenser{
        void success(String data);
        void fail(String data);
    }




    public void deviceBindService(Context context, int type, DeviceListenser listenser){
        this.mContext = context;
        this.currentType = type;
        this.mlistener = listenser;

        context.bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);

    }

    public void deviceUnBindService(){
        mContext.unbindService(serviceConnection);
    }






    /**
     * 服务连接
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
            if (currentType == magcard){
                try {
                    getMagCard();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }else if (currentType == print){

            }else if (currentType == serialNo){
//                try {
//                    getSerialNo();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
            if (currentType == magcard){
                stopSearch();
            }else if (currentType == print){

            }
        }
    };


    public String getSerialNo(){
       String serialNo;
        try {
            Log.e(TAG, "获取AidlSystem实例");
            aidlSystem = AidlSystem.Stub.asInterface(aidlDeviceService.getSystemService());
            serialNo = aidlSystem.getSerialNo();
            Log.e(TAG, serialNo);
            return serialNo;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

       return null;
    }




    private void getMagCard() throws RemoteException {
        Log.e(TAG, "获取磁条卡读卡器实例");
        aidlMagCard = AidlMagCard.Stub.asInterface(aidlDeviceService.getMagCardReader());
        Log.e(TAG, "magcard module init succ");
        searchCard();
    }


    /**
     * 寻卡 获取明文磁道信息
     */
    private void searchCard() {
        try {
            //----------------------磁卡参数--------------------------------------
            final int timeout = 6000;//超时时间

            if (aidlMagCard != null) {
                aidlMagCard.searchCard(timeout, new MagCardListener.Stub() {

                    @Override
                    public void onTimeout() throws RemoteException {
                        Log.e(TAG, "读卡超时");
//                        ma.appendInteractiveInfoAndShow("读卡超时");
                        ToastUtils.CustomShow(mContext, "读卡超时");
                    }

                    @Override
                    public void onSuccess(TrackData trackData) throws RemoteException {
                        Log.e(TAG, "读卡成功!");
                        Log.e(TAG, "cardno:"+trackData.getCardno());
                        mlistener.success(trackData.getCardno());
                    }

                    @Override
                    public void onGetTrackFail() throws RemoteException {
                        Log.e(TAG, "读卡失败");
//                        ma.appendInteractiveInfoAndShow("读卡失败");
                    }

                    @Override
                    public void onError(int errorCode) throws RemoteException {
                        switch (errorCode) {
                            case AidlErrorCode.MagCard.TRACK_DATA_ERR:
                                Log.e(TAG, "刷卡错误——》磁道数据错误");
//                                ma.appendInteractiveInfoAndShow("刷卡错误——》磁道数据错误");
                                break;
                            case AidlErrorCode.MagCard.DEVICE_IS_BUSY:
                                Log.e(TAG, "刷卡错误——》设备忙");
//                                ma.appendInteractiveInfoAndShow("刷卡错误——》设备忙");
                                ToastUtils.CustomShow(mContext, "刷卡错误——》设备忙");
                                break;
                            case AidlErrorCode.MagCard.OTHER_ERROR:
                                Log.e(TAG, "刷卡错误——》其他错误");
//                                ma.appendInteractiveInfoAndShow("刷卡错误——》其他错误");

                                break;

                            default:
                                Log.e(TAG, "未知错误:"+errorCode);
//                                ma.appendInteractiveInfoAndShow("未知错误:"+errorCode);
                                ToastUtils.CustomShow(mContext, "未知错误:"+errorCode);
                                break;
                        }
                    }

                    @Override
                    public void onCanceled() throws RemoteException {
                        Log.e(TAG, "被取消");
//                        ma.appendInteractiveInfoAndShow("被取消");
                        ToastUtils.CustomShow(mContext, "被取消");
                    }
                });
            }else {
                Log.e(TAG, "未检到磁卡设备实例");
                ToastUtils.CustomShow(mContext, "未检到磁卡设备实例");
//                ma.appendInteractiveInfoAndShow("未检到磁卡设备实例");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            ma.appendInteractiveInfoAndShow("searchCard failed:"+e.getMessage());
        }
    }


    /**
     * 停止寻卡
     */
    public void stopSearch() {
        try {
            if (aidlMagCard != null) {
                aidlMagCard.stopSearch();
                Log.e(TAG, "停止寻卡");
//                ma.appendInteractiveInfoAndShow("stopSearch succ");
            }else {
                Log.e(TAG, "未检到磁卡设备实例");
//                ma.appendInteractiveInfoAndShow("未检到磁卡设备实例");
            }
        } catch (Exception e) {
            e.printStackTrace();
//            ma.appendInteractiveInfoAndShow("stopSearch failed:"+e.getMessage());
        }
    }


}

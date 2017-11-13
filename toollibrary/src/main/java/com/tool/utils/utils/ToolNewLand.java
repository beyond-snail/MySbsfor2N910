package com.tool.utils.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.magcard.AidlMagCard;
import com.nld.cloudpos.aidl.magcard.MagCardListener;
import com.nld.cloudpos.aidl.magcard.TrackData;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.printer.AidlPrinterListener;
import com.nld.cloudpos.aidl.printer.PrintItemObj;
import com.nld.cloudpos.aidl.printer.PrintItemObj.ALIGN;
import com.nld.cloudpos.aidl.system.AidlSystem;
import com.nld.cloudpos.data.AidlErrorCode;
import com.nld.cloudpos.data.PrinterConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/10 0010.
 */

public class ToolNewLand {

    private static String TAG = "ToolNewLand";

    private static Context mContext;

    private static ToolNewLand toolNewLand;

    private static AidlDeviceService aidlDeviceService = null;

    /**
     * 打印机
     */
    private static AidlPrinter aidlPrinter = null;

    /**
     * 磁条卡
     */
    private static AidlMagCard aidlMagCard = null;

    /**
     * 系统及设备信息
     */
    private static AidlSystem aidlSystem = null;

    public final static int magcard = 0;
    public final static int print = 1;
    public final static int serialNo = 2;

    private int currentType = -1;
//    private DeviceListener mListener;

    public interface DeviceListener{
        void success(String data);
        void fail(String data);
    }



    private ToolNewLand(Context context, AidlDeviceService aidlDeviceService){
//        this.aidlDeviceService = aidlDeviceService;
        mContext = context;
        try {
            aidlSystem = AidlSystem.Stub.asInterface(aidlDeviceService.getSystemService());
            aidlMagCard = AidlMagCard.Stub.asInterface(aidlDeviceService.getMagCardReader());
            aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public synchronized static ToolNewLand getInstance(Context context, AidlDeviceService aidlDeviceService) {
        if (toolNewLand == null) {
            toolNewLand = new ToolNewLand(context, aidlDeviceService);
        }
        return toolNewLand;
    }


    public static ToolNewLand getToolNewLand(){
        return toolNewLand;
    }




    public String getSerialNo(){
       String serialNo;
        try {
            serialNo = aidlSystem.getSerialNo();
            Log.e(TAG, serialNo);
            return serialNo;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }




    /**
     * 寻卡 获取明文磁道信息
     * @param listener
     */
    public void searchCard(final DeviceListener listener) {
        try {
            //----------------------磁卡参数--------------------------------------
            final int timeout = 40000;//超时时间

            if (aidlMagCard != null) {
                Log.e(TAG, "开始寻卡");
                aidlMagCard.searchCard(timeout, new MagCardListener.Stub() {

                    @Override
                    public void onTimeout() throws RemoteException {
                        Log.e(TAG, "读卡超时");
//                        ma.appendInteractiveInfoAndShow("读卡超时");
//                        ToastUtils.CustomShow(mContext, "读卡超时");
                        listener.fail("读卡超时");
                    }

                    @Override
                    public void onSuccess(TrackData trackData) throws RemoteException {
                        Log.e(TAG, "读卡成功!");
                        Log.e(TAG, "cardno:"+trackData.getCardno());
                        listener.success(trackData.getCardno());
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
//                        ToastUtils.CustomShow(mContext, "被取消");
//                        listener.fail("被取消");
                    }
                });
            }else {
                Log.e(TAG, "未检到磁卡设备实例");
//                ToastUtils.CustomShow(mContext, "未检到磁卡设备实例");
//                ma.appendInteractiveInfoAndShow("未检到磁卡设备实例");
                listener.fail("未检到磁卡设备实例");
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


    /**
     * 获取打印机状态
     */
    public void getPrinterState() {
        try {
            if (aidlPrinter != null) {
                int printerState = aidlPrinter.getPrinterState();
                Log.e(TAG, ""+printerState);
            }else {
                Log.e(TAG, "未检测到打印机模块访问权限");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /**
     * 打印文本
     */
    public void printText() {
        try {
            //--------------------------打印文本-----------------------------
            final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体1", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体2", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体3", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体4", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));

            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体5", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_S,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体6", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体7", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_S,ALIGN.CENTER, false, 6));
            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体8", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_S,ALIGN.CENTER, false, 6));

            data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体9", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_S,ALIGN.CENTER, true, 6));


            data.add(new PrintItemObj("商户名称(MERCHANT NAME)"));
            data.add(new PrintItemObj("商户编号(MERCHANTNO)  123123"));
            data.add(new PrintItemObj("\r"));
            data.add(new PrintItemObj("\r"));
            data.add(new PrintItemObj("\r"));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aidlPrinter != null) {
                        try {
                            aidlPrinter.open();
//                            Bitmap bitmap = BitmapFactory.decodeResource(ma.getResources(), R.drawable.ic_launcher);
//                            aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER, bitmap);
                            aidlPrinter.printText(data);
                            aidlPrinter.start(new AidlPrinterListener.Stub() {

                                @Override
                                public void onPrintFinish() throws RemoteException {
                                    Log.e(TAG, "打印结束");
                                    aidlPrinter.paperSkip(2);
                                }

                                @Override
                                public void onError(int errorCode) throws RemoteException {
                                    Log.e(TAG, "打印异常");
                                }
                            });
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Log.e(TAG, "未检测到打印机模块访问权限");
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

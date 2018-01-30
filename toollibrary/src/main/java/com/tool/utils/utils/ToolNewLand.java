package com.tool.utils.utils;

import android.app.Activity;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.config.ConstantsConfig;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.Couponsn;
import com.model.SbsPrinterData;
import com.model.ShiftRoom;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.magcard.AidlMagCard;
import com.nld.cloudpos.aidl.magcard.MagCardListener;
import com.nld.cloudpos.aidl.magcard.TrackData;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.printer.AidlPrinterListener;
import com.nld.cloudpos.aidl.printer.PrintItemObj;
import com.nld.cloudpos.aidl.printer.PrintItemObj.ALIGN;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.aidl.system.AidlSystem;
import com.nld.cloudpos.data.AidlErrorCode;
import com.nld.cloudpos.data.PrinterConstant;
import com.nld.cloudpos.data.ScanConstant;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RunnableFuture;

import static android.R.attr.format;
import static com.nld.cloudpos.aidl.printer.PrintItemObj.ALIGN.CENTER;

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

    /**
     * 扫码相关
     */
    private static AidlScanner aidlScanner=null;

    public final static int magcard = 0;
    public final static int print = 1;
    public final static int serialNo = 2;

//    private DeviceListener mListener;

    public interface DeviceListener{
        void success(String data);
        void fail(String data);
    }



    private ToolNewLand(Context context, AidlDeviceService aidlDeviceService){
        mContext = context;
        try {
            aidlSystem = AidlSystem.Stub.asInterface(aidlDeviceService.getSystemService());
            aidlMagCard = AidlMagCard.Stub.asInterface(aidlDeviceService.getMagCardReader());
            aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
            aidlScanner= AidlScanner
                    .Stub.asInterface(aidlDeviceService.getScanner());
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
            Log.e(TAG, serialNo+"");
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
    public void printText(final SbsPrinterData printerData) {
        try {

            //判断打印机状态
            if (aidlPrinter != null) {
                int printerState = aidlPrinter.getPrinterState();
                Log.e(TAG, ""+printerState);
                if (printerState == PrinterConstant.PrinterState.PRINTER_STATE_NOPAPER){
                    ToastUtils.CustomShowLong(mContext, "打印机缺纸质...");
                    return;
                }
            }else {
                Log.e(TAG, "未检测到打印机模块访问权限");
                return;
            }


            //--------------------------打印文本-----------------------------
            final List<PrintItemObj> data = new ArrayList<PrintItemObj>();

            switch (printerData.getPayType()){
                case ConstantsConfig.PAY_WAY_RECHARGE_FLOT:
                case ConstantsConfig.PAY_WAY_RECHARGE_ALY:
                case ConstantsConfig.PAY_WAY_RECHARGE_WX: {
                    if (!StringUtils.isBlank(printerData.getRealize_card_num())) {
                        data.add(new PrintItemObj("会员卡号：" + StringUtils.formatSTCardNo(printerData.getRealize_card_num())));
                    }
                    if (!StringUtils.isBlank(printerData.getMember_name())) {
                        data.add(new PrintItemObj("会员姓名：" + printerData.getMember_name()));
                    }
                    if (!StringUtils.isBlank(printerData.getAmount())) {
                        data.add(new PrintItemObj("充值金额：" + printerData.getAmount() + "元"));
                    }
                    int largessAmount = printerData.getOrderAmount() - StringUtils.stringToInt(printerData.getAmount());
                    if (largessAmount != 0) {
                        data.add(new PrintItemObj("充值赠送：" + StringUtils.formatIntMoney(largessAmount) + "元"));
                    }
                    if (printerData.getOrderAmount() != 0) {
                        data.add(new PrintItemObj("到账金额：" + StringUtils.formatIntMoney(printerData.getOrderAmount()) + "元"));
                    }
                    if (printerData.getPacektRemian() != 0) {
                        data.add(new PrintItemObj("账号余额：" + StringUtils.formatIntMoney(printerData.getPacektRemian()) + "元"));
                    }
                }
                    break;
                case ConstantsConfig.PAY_WAY_QB:
                case ConstantsConfig.PAY_WAY_STK:
                    data.add(new PrintItemObj("收款凭证", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, ALIGN.CENTER, false, 6));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("商户名称：" + printerData.getMerchantName()));
                    data.add(new PrintItemObj("终端编号：" + printerData.getTerminalId()));
                    data.add(new PrintItemObj("操作员号：" + printerData.getOperatorNo()));
                    data.add(new PrintItemObj("交易类型：" + "钱包"));
                    data.add(new PrintItemObj("订 单 号：" + printerData.getClientOrderNo()));
                    data.add(new PrintItemObj("日期时间：" + printerData.getDateTime()));
                    data.add(new PrintItemObj("金 额：RMB " + printerData.getAmount()));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));

                    if (printerData.getPacektRemian() != 0){
                        data.add(new PrintItemObj("会员账号余额：" + StringUtils.formatIntMoney(printerData.getPacektRemian()) + "元"));
                    }
                    if (printerData.getPointCoverMoney() != 0) {
                        data.add(new PrintItemObj("积分抵扣金额：" + StringUtils.formatIntMoney(printerData.getPointCoverMoney()) + "元"));
                    }
                    if (printerData.getCouponCoverMoney() != 0) {
                        data.add(new PrintItemObj("优惠券抵扣金额：" + StringUtils.formatIntMoney(printerData.getCouponCoverMoney()) + "元"));
                    }
                    if (printerData.getBackAmt() != 0) {
                        data.add(new PrintItemObj("返利金额：" + StringUtils.formatIntMoney(printerData.getBackAmt()) + "元"));
                    }
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    break;
                case ConstantsConfig.PAY_WAY_CASH:

                    data.add(new PrintItemObj("收款凭证", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, ALIGN.CENTER, false, 6));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("商户名称：" + printerData.getMerchantName()));
                    data.add(new PrintItemObj("终端编号：" + printerData.getTerminalId()));
                    data.add(new PrintItemObj("操作员号：" + printerData.getOperatorNo()));
                    data.add(new PrintItemObj("交易类型：" + "现金/CASH"));
                    data.add(new PrintItemObj("订 单 号：" + printerData.getClientOrderNo()));
                    data.add(new PrintItemObj("日期时间：" + printerData.getDateTime()));
                    data.add(new PrintItemObj("金 额：RMB " + printerData.getReceiveAmount()));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    if (printerData.getPointCoverMoney() != 0) {
                        data.add(new PrintItemObj("积分抵扣金额：" + StringUtils.formatIntMoney(printerData.getPointCoverMoney()) + "元"));
                    }
                    if (printerData.getCouponCoverMoney() != 0) {
                        data.add(new PrintItemObj("优惠券抵扣金额：" + StringUtils.formatIntMoney(printerData.getCouponCoverMoney()) + "元"));
                    }
                    if (printerData.getBackAmt() != 0) {
                        data.add(new PrintItemObj("返利金额：" + StringUtils.formatIntMoney(printerData.getBackAmt()) + "元"));
                    }
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    break;
                case ConstantsConfig.PAY_WAY_PAY_FLOT:
                case ConstantsConfig.PAY_WAY_RECHARGE_CASH: {
                    data.add(new PrintItemObj("收款凭证", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, ALIGN.CENTER, false, 6));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("商户名称：" + printerData.getMerchantName()));
                    data.add(new PrintItemObj("终端编号：" + printerData.getTerminalId()));
                    data.add(new PrintItemObj("操作员号：" + printerData.getOperatorNo()));
                    data.add(new PrintItemObj("交易类型：" + "现金/CASH"));
                    data.add(new PrintItemObj("订 单 号：" + printerData.getClientOrderNo()));
                    data.add(new PrintItemObj("日期时间：" + printerData.getDateTime()));
                    data.add(new PrintItemObj("金 额：RMB " + printerData.getReceiveAmount()));
                    data.add(new PrintItemObj("--------------------------------"));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    if (!StringUtils.isBlank(printerData.getRealize_card_num())) {
                        data.add(new PrintItemObj("会员卡号：" + StringUtils.formatSTCardNo(printerData.getRealize_card_num())));
                    }
                    if (!StringUtils.isBlank(printerData.getMember_name())) {
                        data.add(new PrintItemObj("会员姓名：" + printerData.getMember_name()));
                    }
                    if (!StringUtils.isBlank(printerData.getAmount())) {
                        data.add(new PrintItemObj("充值金额：" + printerData.getAmount() + "元"));
                    }
                    int largessAmount = StringUtils.stringToInt(printerData.getReceiveAmount()) - StringUtils.stringToInt(printerData.getAmount());
                    if (largessAmount != 0) {
                        data.add(new PrintItemObj("充值赠送：" + StringUtils.formatIntMoney(largessAmount) + "元"));
                    }
                    if (printerData.getOrderAmount() != 0) {
                        data.add(new PrintItemObj("到账金额：" + StringUtils.formatIntMoney(printerData.getOrderAmount()) + "元"));
                    }
                    if (printerData.getPacektRemian() != 0) {
                        data.add(new PrintItemObj("账号余额：" + StringUtils.formatIntMoney(printerData.getPacektRemian()) + "元"));
                    }
                }
                    break;
                default:
                    if (printerData.getPointCoverMoney() != 0) {
                        data.add(new PrintItemObj("积分抵扣金额：" + StringUtils.formatIntMoney(printerData.getPointCoverMoney()) + "元"));
                    }
                    if (printerData.getCouponCoverMoney() != 0) {
                        data.add(new PrintItemObj("优惠券抵扣金额：" + StringUtils.formatIntMoney(printerData.getCouponCoverMoney()) + "元"));
                    }
                    if (printerData.getBackAmt() != 0) {
                        data.add(new PrintItemObj("返利金额：" + StringUtils.formatIntMoney(printerData.getBackAmt()) + "元"));
                    }
                    data.add(new PrintItemObj("\r"));
//                    data.add(new PrintItemObj("\r"));
                    break;

            }


            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aidlPrinter != null) {
                        try {
                            aidlPrinter.open();
//                            Bitmap bitmap = BitmapFactory.decodeResource(ma.getResources(), R.drawable.ic_launcher);
//                            aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER, bitmap);
                            aidlPrinter.printText(data);



                            String printContent = (String) SPUtils.get(mContext, "printContent", "");
                            if (!StringUtils.isBlank(printContent)) {
                                data.clear();
                                data.add(new PrintItemObj("\r"));
                                data.add(new PrintItemObj(printContent, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
                                aidlPrinter.printText(data);
                            }

                            if (SPUtils.loadDrawable(mContext) != null){
                                aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER,  SPUtils.loadDrawable(mContext));
                                data.clear();
                                data.add(new PrintItemObj("\r"));
                                aidlPrinter.printText(data);
                            }


                            if (printerData.getPoint_bitmap() != null) {
                                LogUtils.e("width: " + printerData.getPoint_bitmap().getWidth() + " height: "
                                        + printerData.getPoint_bitmap().getHeight());
                                data.clear();
                                data.add(new PrintItemObj("--------------------------------"));
                                data.add(new PrintItemObj("使用微信扫一扫 获取更多优惠信息", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
                                aidlPrinter.printText(data);
                                aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER,  printerData.getPoint_bitmap());
                            }

                            if (printerData.getPoint() > 0) {
                                data.clear();
                                data.add(new PrintItemObj("本次获得积分：" + printerData.getPoint()));
                                aidlPrinter.printText(data);
                            }
                            if (printerData.getPointCurrent() > 0) {
                                data.clear();
                                data.add(new PrintItemObj("积分余额：" + printerData.getPointCurrent()));
                                aidlPrinter.printText(data);
                            }



                            if (!StringUtils.isBlank(printerData.getCouponData())) {
                                try {
                                    data.clear();
                                    data.add(new PrintItemObj("\r"));
                                    data.add(new PrintItemObj("本次消费获得："));
                                    aidlPrinter.printText(data);
                                    data.clear();
                                    Gson gson = new Gson();
                                    List<Couponsn> couponsns = gson.fromJson(printerData.getCouponData(), new TypeToken<List<Couponsn>>() {
                                    }.getType());
                                    if (couponsns != null) {
                                        for (int i = 0; i < couponsns.size(); i++) {
                                            if (couponsns.get(i).getCoupon_type() == 2) {
                                                data.clear();
                                                data.add(new PrintItemObj("折扣券名称：" + couponsns.get(i).getCoupon_name()));
                                                data.add(new PrintItemObj("折扣券折扣：" + couponsns.get(i).getCoupon_money() / 100 + "折"));
                                                aidlPrinter.printText(data);
                                            } else {
                                                data.clear();
                                                data.add(new PrintItemObj("优惠券名称：" + couponsns.get(i).getCoupon_name()));
                                                data.add(new PrintItemObj("优惠券金额：" + StringUtils.formatIntMoney(couponsns.get(i).getCoupon_money())));
                                                aidlPrinter.printText(data);
                                            }
                                        }
                                    }

                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "优惠券信息解析异常。。。");
                                }

                            }

                            if (printerData.getCoupon_bitmap() != null) {
                                LogUtils.e("width: " + printerData.getCoupon_bitmap().getWidth() + " height: "
                                        + printerData.getCoupon_bitmap().getHeight());
                                data.clear();
                                data.add(new PrintItemObj("--------------------------------"));
                                data.add(new PrintItemObj("扫一扫领取红包", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N,ALIGN.CENTER, false, 6));
                                aidlPrinter.printText(data);
                                aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER,  printerData.getCoupon_bitmap());
                            }


                            aidlPrinter.start(new AidlPrinterListener.Stub() {

                                @Override
                                public void onPrintFinish() throws RemoteException {
                                    Log.e(TAG, "打印结束");
                                    aidlPrinter.paperSkip(2);
                                }

                                @Override
                                public void onError(int errorCode) throws RemoteException {
                                    Log.e(TAG, "打印异常"+errorCode);
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


    /**
     * 打印文本
     */
    public void printShiftRoom(final ShiftRoom printerData,final long start_time, final long end_time, final int type) {
        try {

            //判断打印机状态
            if (aidlPrinter != null) {
                int printerState = aidlPrinter.getPrinterState();
                Log.e(TAG, ""+printerState);
                if (printerState == PrinterConstant.PrinterState.PRINTER_STATE_NOPAPER){
                    ToastUtils.CustomShowLong(mContext, "打印机缺纸质...");
                    return;
                }
            }else {
                Log.e(TAG, "未检测到打印机模块访问权限");
                return;
            }


            //--------------------------打印文本-----------------------------
            final List<PrintItemObj> data = new ArrayList<PrintItemObj>();


            if (type == 1) {
                data.add(new PrintItemObj("班接统计", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, ALIGN.CENTER, false, 6));
            }else{
                data.add(new PrintItemObj("当日统计", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, ALIGN.CENTER, false, 6));
            }
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("起始时间："+ StringUtils.timeStamp2Date1(start_time + "")+":00"));
            data.add(new PrintItemObj("结束时间："+ StringUtils.timeStamp2Date1(end_time + "")+":00"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("刷卡："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_swipe() != null) ? printerData.getPay_swipe().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收金额："+ ((printerData.getPay_swipe() != null) ? StringUtils.formatIntMoney(printerData.getPay_swipe().getReal_pay_money()) : "0")+"元"));
            data.add(new PrintItemObj("优惠券抵扣金额："+ ((printerData.getPay_swipe() != null) ? StringUtils.formatIntMoney(printerData.getPay_swipe().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣金额："+ ((printerData.getPay_swipe() != null) ? StringUtils.formatIntMoney(printerData.getPay_swipe().getIntergral_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("现金："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_cash() != null) ? printerData.getPay_cash().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收金额："+ ((printerData.getPay_cash() != null) ? StringUtils.formatIntMoney(printerData.getPay_cash().getReal_pay_money()) : "0") +"元"));
            data.add(new PrintItemObj("优惠券抵扣金额："+ ((printerData.getPay_cash() != null) ? StringUtils.formatIntMoney(printerData.getPay_cash().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣金额："+ ((printerData.getPay_cash() != null) ? StringUtils.formatIntMoney(printerData.getPay_cash().getIntergral_deduct())  : "0")+"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("微信："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_wx() != null) ? printerData.getPay_wx().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收金额："+ ((printerData.getPay_wx() != null) ? StringUtils.formatIntMoney(printerData.getPay_wx().getReal_pay_money()) : "0") +"元"));
            data.add(new PrintItemObj("优惠券抵扣金额："+ ((printerData.getPay_wx() != null) ? StringUtils.formatIntMoney(printerData.getPay_wx().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣金额："+ ((printerData.getPay_wx() != null) ? StringUtils.formatIntMoney(printerData.getPay_wx().getIntergral_deduct())  : "0")+"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("支付宝："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_aly() != null) ? printerData.getPay_aly().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收金额："+ ((printerData.getPay_aly() != null) ? StringUtils.formatIntMoney(printerData.getPay_aly().getReal_pay_money()) : "0") +"元"));
            data.add(new PrintItemObj("优惠券抵扣金额："+ ((printerData.getPay_aly() != null) ? StringUtils.formatIntMoney(printerData.getPay_aly().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣金额："+ ((printerData.getPay_aly() != null) ? StringUtils.formatIntMoney(printerData.getPay_aly().getIntergral_deduct())  : "0")+"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("钱包："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_qb() != null) ? printerData.getPay_qb().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收金额："+ ((printerData.getPay_qb() != null) ? StringUtils.formatIntMoney(printerData.getPay_qb().getReal_pay_money()) : "0") +"元"));
            data.add(new PrintItemObj("优惠券抵扣金额："+ ((printerData.getPay_qb() != null) ? StringUtils.formatIntMoney(printerData.getPay_qb().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣金额："+ ((printerData.getPay_qb() != null) ? StringUtils.formatIntMoney(printerData.getPay_qb().getIntergral_deduct())  : "0")+"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("刷卡撤销："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_unswipe() != null) ? printerData.getPay_unswipe().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("撤销金额："+ ((printerData.getPay_unswipe() != null) ? StringUtils.formatIntMoney(printerData.getPay_unswipe().getReal_undo_money()) : "0") +"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("微信撤销："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_unwx() != null) ? printerData.getPay_unwx().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("撤销金额："+ ((printerData.getPay_unwx() != null) ? StringUtils.formatIntMoney(printerData.getPay_unwx().getReal_undo_money()) : "0") +"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("支付宝撤销："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_unaly() != null) ? printerData.getPay_unaly().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("撤销金额："+ ((printerData.getPay_unaly() != null) ? StringUtils.formatIntMoney(printerData.getPay_unaly().getReal_undo_money()) : "0") +"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("钱包撤销："));
            data.add(new PrintItemObj("交易笔数："+ ((printerData.getPay_unqb() != null) ? printerData.getPay_unqb().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("撤销金额："+ ((printerData.getPay_unqb() != null) ? StringUtils.formatIntMoney(printerData.getPay_unqb().getReal_undo_money()) : "0") +"元"));
            data.add(new PrintItemObj("--------------------------------"));
            data.add(new PrintItemObj("交易统计："));
            data.add(new PrintItemObj("交易总笔数："+ ((printerData.getTotal() != null) ? printerData.getTotal().getTrade_num() : "0")+"笔"));
            data.add(new PrintItemObj("实收总金额："+ ((printerData.getTotal() != null) ? StringUtils.formatIntMoney(printerData.getTotal().getReal_pay_money()) : "0")+"元"));
            data.add(new PrintItemObj("交易撤销总金额："+ ((printerData.getTotal() != null) ? StringUtils.formatIntMoney(printerData.getTotal().getReal_undo_money()) : "0")+"元"));
            data.add(new PrintItemObj("优惠券抵扣总金额："+ ((printerData.getTotal() != null) ? StringUtils.formatIntMoney(printerData.getTotal().getCoupon_deduct()) : "0")+"元"));
            data.add(new PrintItemObj("积分抵扣总金额："+ ((printerData.getTotal() != null) ? StringUtils.formatIntMoney(printerData.getTotal().getIntergral_deduct()) : "0") +"元"));
            data.add(new PrintItemObj("\r"));
            data.add(new PrintItemObj("\r"));

            data.add(new PrintItemObj("\r"));
            data.add(new PrintItemObj("\r"));


            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (aidlPrinter != null) {
                        try {
                            aidlPrinter.open();
                            aidlPrinter.printText(data);


                            aidlPrinter.start(new AidlPrinterListener.Stub() {

                                @Override
                                public void onPrintFinish() throws RemoteException {
                                    Log.e(TAG, "打印结束");
                                    aidlPrinter.paperSkip(2);
                                }

                                @Override
                                public void onError(int errorCode) throws RemoteException {
                                    Log.e(TAG, "打印异常"+errorCode);
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



    public void scan(final DeviceListener listener){
        try {
            Log.i(TAG, "-------------scan-----------");
//            aidlScanner= AidlScanner
//                    .Stub.asInterface(aidlDeviceService.getScanner());
            aidlScanner.startScan(ScanConstant.ScanType.BACK, 20, new AidlScannerListener.Stub() {

                @Override
                public void onScanResult(String[] arg0) throws RemoteException {
                    Log.e(TAG,"onScanResult-----"+arg0[0]);
                    listener.success(arg0[0]);

                }

                @Override
                public void onFinish() throws RemoteException {

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
                    listener.fail(arg0+"------"+arg1);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void stopscan(){
        try {
            Log.i(TAG, "------------stopscan-----------");
            aidlScanner.stopScan();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

package com.zfsbs.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myokhttp.MyOkHttp;
import com.myokhttp.response.JsonResponseHandler;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.dialog.SignDialog;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.view.MyGridView;
import com.zfsbs.R;
import com.zfsbs.adapter.MyMenuAdapter;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.config.EnumConstsSbs;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.Menu;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class SaleMainActivity extends BaseActivity{


    private String TAG = "SaleMainActivity";

//    private RelativeLayout btnSale;
//    private RelativeLayout btnyxfSale;
//    private RelativeLayout btnyxfSaleManager;
//    private RelativeLayout btnhd;
//
//    private RelativeLayout btnRecord;
//    private RelativeLayout btnSaleManager;
//    private RelativeLayout btnSaleInfo;
//    private RelativeLayout btnGetInfo;
//    private RelativeLayout btnChangePass;
//    private RelativeLayout btnEndQuery;
//    private RelativeLayout btnShitRoom;
//    private RelativeLayout btnRicher_e_qb;
//    private RelativeLayout btnVerification;
//
//    private LinearLayout ll1;
//    private LinearLayout ll2;
//    private LinearLayout ll3;
//    private LinearLayout ll4;
//    private LinearLayout ll5;
//    private LinearLayout ll6;
//    private LinearLayout ll7;


    private List<View> views = null;

    public ViewPager mViewPager;
    public LinearLayout viewPoints;
    private SbsPrinterData printerData;


    private List<Menu> list = new ArrayList<Menu>();
    private MyGridView gridView;
    private MyMenuAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_main3);
        mContext = this;
//        AppManager.getAppManager().addActivity(this);
        initTitle("首页");
        initView();
//        addLinstener();

        initData();



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
//        if (!NetUtils.isServiceWork(SaleMainActivity.this, "com.zfsbs.myservice.AlarmService")){
//            LogUtils.e("Start Service", "开启定时服务。。。。");
//            Intent i = new Intent(SaleMainActivity.this, AlarmService.class);
//            SaleMainActivity.this.startService(i);
//        }

//        if (!SystemUtils.isServiceWork(this, "com.zfsbs.service.UploadService")){
//            Intent intent = new Intent(this,UploadService.class);
//            startService(intent);
//        }
    }







    private void initView() {


        for (int i = 0; i < EnumConstsSbs.MenuType.values().length; i++){
            Menu menu = new Menu();
            menu.setBg(EnumConstsSbs.MenuType.values()[i].getBg());
            menu.setName(EnumConstsSbs.MenuType.values()[i].getName());
            list.add(menu);
        }



        gridView = (MyGridView) findViewById(R.id.id_gridview);
        adapter = new MyMenuAdapter(mContext, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick " + position);
                // 下拉刷新占据一个位置
                int index = EnumConstsSbs.MenuType.getCodeByName(list.get(position).getName());
                switch (index){
                    case 1:
                        SPUtils.put(SaleMainActivity.this, Config.APP_TYPE, Config.APP_SBS);

                        CommonFunc.startAction(SaleMainActivity.this, InputAmountActivity.class, false);

                        break;
                    case 2:
                        CommonFunc.startAction(SaleMainActivity.this, RecordInfoActivity.class, false);
                        break;
                    case 3:
                        CommonFunc.startAction(SaleMainActivity.this, CheckOperatorLoginActivity.class, false);
                        break;
                    case 4:
                        CommonFunc.startAction(SaleMainActivity.this, OpenCardActivity.class, false);
                        break;
                    case 5:
                        CommonFunc.startAction(SaleMainActivity.this, VerificationActivity.class, false);
//                        CommonFunc.startAction(SaleMainActivity.this, TestPopActivity.class, false);

                        break;
                    case 6:
                        CommonFunc.startAction(SaleMainActivity.this, SysMainActivity.class, false);
                        break;
                }
            }
        });

    }



//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.id_ll_sale:
//                SPUtils.put(this, Config.APP_TYPE, Config.APP_SBS);
//                if (isCheckStatus()) {
//                    CommonFunc.startAction(this, InputAmountActivity.class, false);
//                } else {
//                    CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                }
//                break;
//            case R.id.id_ll_record:
//                CommonFunc.startAction(this, RecordInfoActivity.class, false);
//                break;
//            case R.id.id_ll_member_recharge:
//                CommonFunc.startAction(this, CheckOperatorLoginActivity.class, false);
//                break;
//            case R.id.id_ll_open_card:
//                CommonFunc.startAction(this, OpenCardActivity.class, false);
//                break;
//            case R.id.id_ll_system_setting:
//                CommonFunc.startAction(this, SysMainActivity.class, false);
//                break;
////            case R.id.id_ll_change_pass:
////                CommonFunc.startAction(this, CardChangeActivity.class, false);
////                break;
//            case R.id.id_ll_id_sn_verification:
//                CommonFunc.startAction(this, VerificationActivity.class, false);
//                break;
//        }

//            case R.id.id_ll_sale:
//                SPUtils.put(this, Config.APP_TYPE, Config.APP_SBS);
////                if (getLoginDataNoMember()) {
//                    if (isCheckStatus()) {
//
//                        CommonFunc.startAction(this, InputAmountActivity.class, false);
//                    } else {
////                        SPUtils.put(this, Config.APP_TYPE, Config.APP_SBS);
//                        CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                    }
////                }else {
////                    getInfo(false);
////                }
//                break;
//            case R.id.id_ll_yxf_sale:
//                SPUtils.put(this, Config.APP_TYPE, Config.APP_YXF);
//                if (isCheckStatus()) {
//                    if (!StringUtils.isEmpty((String) SPUtils.get(this, Config.YXF_MERCHANT_ID, Config.YXF_DEFAULT_MERCHANTID))) {
//
//                        CommonFunc.startAction(this, InputAmountActivity.class, false);
//                    } else {
//                        CommonFunc.startAction(this, YxfManagerActivity.class, false);
//                    }
//                } else {
////                    SPUtils.put(this, Config.APP_TYPE, Config.APP_YXF);
//                    CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                }
//                break;
//            case R.id.id_ll_hd:
//                SPUtils.put(this, Config.APP_TYPE, Config.APP_HD);
//                if (isCheckStatus()) {
//
//                    CommonFunc.startAction(this, InputAmountActivity.class, false);
//                } else {
////                    SPUtils.put(this, Config.APP_TYPE, Config.APP_HD);
//                    CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                }
//                break;
//            case R.id.id_ll_yxf_sale_manager:
//                CommonFunc.startAction(this, YxfManagerActivity.class, false);
//                break;
//            case R.id.id_ll_record:
//                CommonFunc.startAction(this, RecordInfoActivity.class, false);
//                break;
//            case R.id.id_ll_sale_manager:
//                CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                break;
//            case R.id.id_ll_sale_info:
//                CommonFunc.startAction(this, LoginInfoActivity.class, false);
//                break;
//            case R.id.id_ll_getInfo:
//                if (Config.OPERATOR_UI_BEFORE) {
//                    CommonFunc.startAction(this, GetLoginInfoActivity.class, false);
//                }else {
//                    CommonFunc.startAction(this, GetLoginInfoActivity1.class, false);
//                }
//                break;
//            case R.id.id_ll_change_pass:
//                CommonFunc.startAction(this, ChangePassActivity.class, false);
//                break;
//            case R.id.id_ll_end_query:
//                endQuery1();
//                break;
//            case R.id.id_ll_shiftroom:
//                CommonFunc.startAction(this, ShiftRoomActivity.class, false);
////                final SignDialog dialog = new SignDialog(SaleMainActivity.this, new SignDialog.OnClickInterface() {
////                    @Override
////                    public void onClickSure(Bitmap bitmap) {
////
////                    }
////
////                });
////                dialog.show();
//                break;
//            case R.id.id_ll_richer_e_qb:
//                SPUtils.put(this, Config.APP_TYPE, Config.APP_Richer_e);
////                if (getLoginDataIsMember()) {
//                    if (isCheckStatus()) {
//
//                        CommonFunc.startAction(this, InputAmountActivity.class, false);
//                    } else {
//                        CommonFunc.startAction(this, HsSaleManagerActivity.class, false);
//                    }
////                }else {
////                    getInfo(true);
////                }
//                break;
//            default:
//                break;
//        }
//    }


//    //获取当前是否存在会员密钥
//    private boolean getLoginDataIsMember(){
//        List<LoginApiResponse> list = DataSupport.findAll(LoginApiResponse.class);
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getKeyIndex() == Constants.FY_INDEX_1) {
//                //设置当前使用的是会员信息
//                MyApplication.getInstance().setLoginData(list.get(i));
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean getLoginDataNoMember(){
//        List<LoginApiResponse> list = DataSupport.findAll(LoginApiResponse.class);
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getKeyIndex() == Constants.FY_INDEX_0) {
//                //设置当前使用的是会员信息
//                MyApplication.getInstance().setLoginData(list.get(i));
//                return true;
//            }
//        }
//        return false;
//    }
//
//
//
//    private void getInfo(final boolean isMember) {
//        final LoginAction loginAction = new LoginAction(SaleMainActivity.this, new UiAction() {
//
//            @Override
//            public void UiResultAction(Activity context, Class<?> cls, Bundle bundle, int requestCode) {
//                LogUtils.e("UiResultAction01");
//            }
//
//            @Override
//            public void UiAction(Activity context, Class<?> cls, Bundle bundle, boolean flag) {
//                LogUtils.e("UiAction01");
//            }
//
//            @Override
//            public void UiAction(Activity context, Class<?> cls, boolean flag) {
//                LogUtils.e("UiAction02");
//                if (isMember) {
//                    if (isCheckStatus()) {
//                        SPUtils.put(SaleMainActivity.this, Config.APP_TYPE, Config.APP_Richer_e);
//                        startAction(SaleMainActivity.this, InputAmountActivity.class, false);
//                    } else {
//                        startAction(SaleMainActivity.this, HsSaleManagerActivity.class, false);
//                    }
//                }else {
//                    if (isCheckStatus()) {
//                        SPUtils.put(SaleMainActivity.this, Config.APP_TYPE, Config.APP_SBS);
//                        startAction(SaleMainActivity.this, InputAmountActivity.class, false);
//                    } else {
//                        startAction(SaleMainActivity.this, HsSaleManagerActivity.class, false);
//                    }
//                }
//
//            }
//        });
//
//        loginAction.loginActionMember();
//    }


    /**
     * 末笔查询
     */
//    private void endQuery1() {
//
//        if (CommonFunc.recoveryFailureInfo(this) == null) {
//            ToastUtils.CustomShow(this, "末笔交易成功，无需查询");
//            return;
//        }
//        switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
//            case Constants.PAY_WAY_QB:
//                ZfQbQuery();
//                break;
//            case Constants.PAY_WAY_ALY:
//            case Constants.PAY_WAY_WX:
//                if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
//                    ZfFyPayQuery();
//                } else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
//                    ZfFyQuery();
//                }
//                break;
//        }
//    }




    private FyBat.FYPayResultEvent listener1 = new FyBat.FYPayResultEvent() {
        @Override
        public void onSuccess(FyMicropayResponse data) {


            setFySmPay1(data);
        }

        @Override
        public void onSuccess(FyQueryResponse data) {
            //先判断本地数据是否存在，防止从华尔街平台拿到的是上一笔成功的交易
            SbsPrinterData datas = DataSupport.findLast(SbsPrinterData.class);
            if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
                ToastUtils.CustomShow(SaleMainActivity.this, "请确认消费者交易成功。");
                return;
            }
            setFySmPayQurey1(data);
        }

        @Override
        public void onSuccess(FyRefundResponse data) {

        }

        @Override
        public void onFailure(int statusCode, String error_msg, String pay_type, String amount) {

        }

        @Override
        public void onFailure(FyMicropayRequest data) {

        }

        @Override
        public void onFailure(FyQueryRequest data) {

        }

        @Override
        public void onLogin() {

        }
    };

    /**
     * 富友扫码支付异常处理
     */
    private void ZfFyPayQuery() {
        printerData = new SbsPrinterData();
        FyBat fybat = new FyBat(this, listener1);
        fybat.terminalQuery1(CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getAmount(), true,
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }

    /**
     * 富友扫码查询异常处理
     */
    private void ZfFyQuery() {
        printerData = new SbsPrinterData();
        FyBat fybat = new FyBat(this, listener1);
        fybat.query1(this, CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getOrderNo(),
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }


    /**
     * 富友扫码支付参数设置
     *
     * @param data
     */
    private void setFySmPay1(FyMicropayResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.formatTime(data.getTxn_begin_ts()));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getTotal_amount()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext, printerData, CommonFunc.recoveryMemberInfo(this),
                    data.getOutOrderNum(), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(data.getOutOrderNum());
            transUploadAction1(request);
        }

    }


    /**
     * 扫码支付查询异常
     *
     * @param data
     */
    private void setFySmPayQurey1(FyQueryResponse data) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getFyMerchantName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        printerData.setTerminalId(StringUtils.getTerminalNo(StringUtils.getSerial()));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(data.getTransaction_id());
        printerData.setAuthCode(data.getMchnt_order_no());
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatStrMoney(data.getOrder_amt()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
            printerData.setPayType(Constants.PAY_WAY_ALY);
        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
            printerData.setPayType(Constants.PAY_WAY_WX);
        }

        if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext, printerData, CommonFunc.recoveryMemberInfo(this),
                    data.getOutOrderNum(), printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(data.getOutOrderNum());
            transUploadAction1(request);
        }

    }

    /**
     * 设置钱包参数
     *
     * @param data
     * @param orderNo
     * @param time
     * @param traceNum
     */
//    private void setQbPay1(ZfQbResponse data, String orderNo, String time, String traceNum, String cardNo) {
//        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
//        printerData.setMerchantNo(data.getGroupId());
//        printerData.setTerminalId(StringUtils.getSerial());
//        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
//        printerData.setCardNo(cardNo);
//        printerData.setDateTime(time);
//        printerData.setClientOrderNo(orderNo);
//        printerData.setTransNo(traceNum);
//        printerData.setAuthCode(data.getSystemOrderNo());
//        printerData.setDateTime(StringUtils.formatTime(time));
//        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
//        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
//        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
//        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
//        printerData.setPayType(Constants.PAY_WAY_QB);
//
//        if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_SBS) {
//            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
//                    CommonFunc.getNewClientSn(), printerData.getTransNo(), printerData.getAuthCode()
//            );
//            //这个地方保持和支付的时候一直
//            request.setClientOrderNo(orderNo);
//            if (StringUtils.isEmpty(request.getCardNo())) {
//                request.setCardNo(cardNo);
//            }
//            transUploadAction1(request);
//        } else if (CommonFunc.recoveryFailureInfo(this).getApp_type() == Config.APP_HD) {
//            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
//                    CommonFunc.getNewClientSn(), printerData.getTransNo(), printerData.getAuthCode()
//            );
//            //这个地方保持和支付的时候一直
//            request.setClientOrderNo(orderNo);
//            transUploadAction2(request);
//        }
//
//
//    }

    /**
     * 流水上送
     *
     * @param request
     */
    private void transUploadAction1(final TransUploadRequest request) {
        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("正在上传交易流水...");
        dialog.setCancelable(false);
        this.sbsAction.transUpload(this, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {

                setTransUpLoadData(request);
                // 设置流水返回的数据
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(SaleMainActivity.this, errorEvent + "#" + message);


                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(CommonFunc.recoveryFailureInfo(SaleMainActivity.this).getApp_type());
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, SaleMainActivity.this);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(SaleMainActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(SaleMainActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }



    private void setTransUpLoadData(TransUploadRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
        LogUtils.e(data);
        printerData.setTransUploadData(data);
    }


    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
            Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
            printerData.setPoint_bitmap(point_bitmap);
            printerData.setCoupon_bitmap(title_bitmap);


            // 打印
//            Printer.print(printerData, SaleMainActivity.this);

        }

        ;
    };


    private void setCounponData(List<Couponsn> data) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String counponStr = gson.toJson(data);
        printerData.setCouponData(counponStr);
    }

    protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
        printerData.setPoint_url(data.getPoint_url());
        printerData.setPoint(data.getPoint());
        printerData.setPointCurrent(data.getPointCurrent());
        setCounponData(data.getCoupon());
        printerData.setBackAmt(data.getBackAmt());
        printerData.setApp_type(CommonFunc.recoveryFailureInfo(this).getApp_type());
        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {

                Bitmap point_bitmap = Constants.ImageLoad(data.getPoint_url());
                Bitmap title_bitmap = Constants.ImageLoad(data.getCoupon_url());
                dialog.dismiss();

                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putParcelable("point_bitmap", point_bitmap);
                bundle.putParcelable("title_bitmap", title_bitmap);
                msg.setData(bundle);
                mhandler.sendMessage(msg);

            }
        }).start();

    }


    private void PrinterDataSave() {
        CommonFunc.ClearFailureInfo(this);
        CommonFunc.PrinterDataDelete();
        printerData.setStatus(true);
        if (printerData.save()) {
            LogUtils.e("打印数据存储成功");
        } else {
            LogUtils.e("打印数据存储失败");
        }
    }


//    private boolean isCheckStatus() {
//
//        if (!MyApplication.getInstance().getLoginData().isDownMasterKey()) {
////            DownMasterKey();
//            ToastUtils.CustomShow(this, "请下载主密钥。。。");
//            return false;
//        }
//
//        if (CommonFunc.isLogin(this, Constants.HS_LOGIN_TIME, Constants.DEFAULT_HS_LOGIN_TIME)) {
////            Hslogin();
//            ToastUtils.CustomShow(this, "请签到。。。");
//            return false;
//        }
//
//        return true;
//    }
}

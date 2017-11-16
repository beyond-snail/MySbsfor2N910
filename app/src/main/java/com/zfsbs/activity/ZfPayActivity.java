package com.zfsbs.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.dialog.PassWordDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.KeyBoardUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.tool.utils.view.MyGridView;
import com.zfsbs.R;
import com.zfsbs.adapter.MyPayTypeAdapter;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.config.EnumConstsSbs;
import com.zfsbs.core.action.BATPay;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.ZfQbAction;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.PayType;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.model.SmResponse;
import com.zfsbs.model.StkPayRequest;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.getNewClientSn;
import static com.zfsbs.common.CommonFunc.startAction;
import static com.zfsbs.common.CommonFunc.startResultAction;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_QB;
import static com.zfsbs.config.Constants.REQUEST_CASH;

public class ZfPayActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";


    private List<PayType> list = new ArrayList<PayType>();
    private MyGridView gridView;
    private MyPayTypeAdapter adapter;

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;
    private LinearLayout btnPayflot;
    private LinearLayout btnCash;
    private LinearLayout btnAly;
    private LinearLayout btnWx;
    private LinearLayout btnQb;
    private LinearLayout btnPayStk;
    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private ScrollView ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_no_pay_amount;
    private LinearLayout ll_payQuery;

    private LinearLayout ll_pointAmount;
    private LinearLayout ll_couponAmount;

    //    private int amount;
    private int orderAmount;

    private BATPay bat;
    private FyBat fybat;
    private ZfQbAction qbpay;
//	private FyBat.FYPayResultEvent fyPayResultEvent;

    private MemberTransAmountResponse memberTransAmount;
    //    private MemberTransAmountResponse getMemberData;
    private String goundId;


    private String phone = ""; //手机号
//    private boolean isYxf = false; //是否是第三方
//	private boolean isYxfUpload = false; //是否上送流水

    private int app_type = 0;


    private String clientNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_pay_type);
//        AppManager.getAppManager().addActivity(this);
        initTitle("收银");

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        initView();
        getData();
        addListenster();

    }

    private void initView() {

        setPayTypeUi();

        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (ScrollView) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);
        ll_no_pay_amount = (LinearLayout) findViewById(R.id.ll_no_pay_amount);

        ll_pointAmount = (LinearLayout) findViewById(R.id.id_ll_pointAmount);
        ll_couponAmount = (LinearLayout) findViewById(R.id.id_ll_coupon_amount);



    }

    private void setPayTypeUi() {

        for (int i = 0; i < EnumConstsSbs.PaymentType.values().length; i++){
            PayType type = new PayType();
            type.setIcon(EnumConstsSbs.PaymentType.values()[i].getBg());
            type.setName(EnumConstsSbs.PaymentType.values()[i].getName());
            list.add(type);
        }

        gridView = (MyGridView) findViewById(R.id.id_gridview);
        adapter = new MyPayTypeAdapter(mContext, list);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "onItemClick " + position);
                // 下拉刷新占据一个位置
                int index = EnumConstsSbs.PaymentType.getCodeByName(list.get(position).getName());
                switch (index){
                    case 1:
                        payflot1();
                        break;
                    case 2:
                        payBat(Constants.PAY_WAY_ALY);
                        break;
                    case 3:
                        payBat(Constants.PAY_WAY_WX);
                        break;
                    case 4:
                        payBat(Constants.PAY_WAY_UNIPAY);
                        break;
                    case 5:
                        Bundle bundle = new Bundle();
                        bundle.putString("amount", tPayAmount.getText().toString());
                        startResultAction((Activity) mContext, ZfPayCashActivity.class, bundle, REQUEST_CASH);
                        break;
                    case 6:
//                        startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_QB);
                        ToolNewLand.getToolNewLand().scan(new ToolNewLand.DeviceListener() {
                            @Override
                            public void success(final String data) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!StringUtils.isBlank(data)) {
                                            ZfQBPay2(data, Constants.PAY_WAY_QB);
                                        }
                                    }
                                });

                            }

                            @Override
                            public void fail(final String data) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.CustomShow(mContext, data);
                                    }
                                });
                            }
                        });
                        break;
                    case 7:
                        if (StringUtils.isBlank(CommonFunc.recoveryMemberInfo(mContext).getStkCardNo())){
                            ToastUtils.CustomShow(mContext, "请选择其他支付方式");
                            return;
                        }
                        inputCardPass();
                        break;
                }
            }
        });
    }


    private void getData() {
        MemberTransAmountResponse getMemberData = CommonFunc.recoveryMemberInfo(this);
        if (getMemberData != null) {
            tOrderAmount.setText(StringUtils.formatIntMoney(getMemberData.getTradeMoney()));
            tPayAmount.setText(StringUtils.formatIntMoney(getMemberData.getRealMoney()));
            tPayPointAmount.setText(StringUtils.formatIntMoney(getMemberData.getPointCoverMoney()));
            tPayCouponAmount.setText(StringUtils.formatIntMoney(getMemberData.getCouponCoverMoney()));
            if (getMemberData.getRealMoney() == 0) {
                ll_no_pay_amount.setVisibility(View.VISIBLE);
                ll_payType.setVisibility(View.GONE);
            }
        }


        //获取订单号
        SetClientOrder setClientOrder = CommonFunc.recoveryClientOrderNo(this);
        if (setClientOrder != null) {
            if (setClientOrder.isStatus()) {
                //是会员
                clientNo = setClientOrder.getClientNo();
            } else {
                //是会员 但 不使用权益 ，和 不是会员一样处理
                clientNo = getNewClientSn(mContext);
            }
        } else {
            clientNo = getNewClientSn(mContext);
        }

        qbpay = new ZfQbAction(this);

        if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
            printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
        }


    }




    private void showLayoutEndQuery() {
        ll_payType.setVisibility(View.GONE);
        ll_payQuery.setVisibility(View.VISIBLE);
    }


    private void addListenster() {
        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        CommonFunc.startAction(this, InputAmountActivity.class, true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ll_payType.getVisibility() == View.VISIBLE || ll_no_pay_amount.getVisibility() == View.VISIBLE) {
            sbsAction.OrderCancel(this, clientNo);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:
                    if (printerData.getPayType() == Constants.PAY_WAY_STK || printerData.getPayType() == Constants.PAY_WAY_QB ||
                            printerData.getPayType() == Constants.PAY_WAY_CASH){
                        ToolNewLand.getToolNewLand().printText(printerData);
                    }else {
                        if (printerData.getPayType() == Constants.PAY_WAY_FLOT) {
                            CommonFunc.query(this, 0, printerData.getClientOrderNo(), printerData.getBatchNO() + printerData.getVoucherNo());
                        } else if (printerData.getPayType() == Constants.PAY_WAY_ALY || printerData.getPayType() == Constants.PAY_WAY_WX){
                            CommonFunc.query(this, 1, printerData.getClientOrderNo(), printerData.getBatchNO() + printerData.getVoucherNo());
                        }
                    }

                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                startAction(this, InputAmountActivity.class, true);
            }
            break;
            case R.id.id_no_pay_amount:
                setNoPayAmount1();
                break;
            case R.id.id_query:
                setLastQuerySend1();

                break;
            default:
                break;
        }
    }



    /**
     * 实体卡密码支付
     */
    private void inputCardPass() {
        final PassWordDialog dialog = new PassWordDialog(mContext, R.layout.activity_psw, new PassWordDialog.OnResultInterface() {

            @Override
            public void onResult(String data) {
                LogUtils.e(data);
                ZfStkPay(data, Constants.PAY_WAY_STK);
            }
        });
        dialog.setCancelable(true);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                KeyBoardUtils.showSoftInput(ZfPayActivity.this);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                KeyBoardUtils.hideSoftInput(ZfPayActivity.this);
            }
        });
        dialog.show();
    }

    /**
     * 实体卡支付
     * @param psw
     */
    private void ZfStkPay(final String psw, final int pay_type ){


        final String time = StringUtils.getFormatCurTime();
        final String traceNum = StringUtils.getFormatCurTime() + StringUtils.createRandomNumStr(5);


        final StkPayRequest request = new StkPayRequest();
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setCardNo(CommonFunc.recoveryMemberInfo(this).getMemberCardNo());
        request.setActivateCode(ToolNewLand.getToolNewLand().getSerialNo());
        request.setOrderNo(clientNo);
        request.setPayPassword(psw);
        request.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        request.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));
        request.setT(StringUtils.getdate2TimeStamp(StringUtils.formatTime(time)));
        request.setTransNo(traceNum);
        request.setPayAmount(CommonFunc.recoveryMemberInfo(this).getRealMoney());


        final LoadingDialog dialog = new LoadingDialog(mContext);
        dialog.show("加载中...");
        dialog.setCancelable(false);
        this.sbsAction.StkPay(mContext, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {
                setStkPay(clientNo, time, traceNum, pay_type);
                setStkRequestData(request);
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);

                if ("0".equals(errorEvent)){
                    showLayoutEndQuery();
                    //设置末笔查询数据
                    setQbFailureQuery(clientNo, time, traceNum, Constants.PAY_WAY_STK, CommonFunc.recoveryMemberInfo(mContext).getMemberCardNo());
                }
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    /**
     * 钱包支付
     */
    private void ZfQBPay2(String result_qb, final int pay_type ){


        final String time = StringUtils.getFormatCurTime();
        final String traceNum = StringUtils.getFormatCurTime() + StringUtils.createRandomNumStr(5);
        final String temp[] = result_qb.split("&");

        final StkPayRequest request = new StkPayRequest();
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        if (CommonFunc.recoveryMemberInfo(this) != null && !StringUtils.isBlank(CommonFunc.recoveryMemberInfo(this).getMemberCardNo())){
            request.setCardNo(CommonFunc.recoveryMemberInfo(this).getMemberCardNo());
        }else{
            request.setCardNo(temp[1]);
        }

        request.setActivateCode(ToolNewLand.getToolNewLand().getSerialNo());
        request.setOrderNo(clientNo);
        request.setQrCode(temp[0]);
        request.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        request.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));
        request.setT(StringUtils.getdate2TimeStamp(StringUtils.formatTime(time)));
        request.setTransNo(traceNum);
        request.setPayAmount(CommonFunc.recoveryMemberInfo(this).getRealMoney());


        final LoadingDialog dialog = new LoadingDialog(mContext);
        dialog.show("加载中...");
        dialog.setCancelable(false);
        this.sbsAction.qbPay(mContext, request, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {
                setStkPay(clientNo, time, traceNum, pay_type);
                setStkRequestData(request);
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                if ("0".equals(errorEvent)){
                    showLayoutEndQuery();
                    //设置末笔查询数据
                    setQbFailureQuery(clientNo, time, traceNum, Constants.PAY_WAY_QB, temp[1]);
                }
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }


    private void setStkPay(String orderNo, String time, String traceNum, int pay_type) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo("");
        printerData.setTerminalId(ToolNewLand.getToolNewLand().getSerialNo());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(time);
        printerData.setClientOrderNo(orderNo);
        printerData.setTransNo(traceNum);
        printerData.setDateTime(StringUtils.formatTime(time));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(pay_type);
    }




    private void setNoPayAmount1() {
        setCashPrintData1(0);
        //设置流水上送参数
        TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData, CommonFunc.recoveryMemberInfo(this),
                clientNo, "", ""
        );
        //打印订单号与流水上送统一
        printerData.setClientOrderNo(request.getClientOrderNo());
        //流水上送
        transUploadAction1(request);
    }


    private void setLastQuerySend1() {

        switch (CommonFunc.recoveryFailureInfo(this).getPay_type()) {
            case Constants.PAY_WAY_QB:
            case Constants.PAY_WAY_STK:
                ZfQbQuery();
                break;
            case Constants.PAY_WAY_ALY:
            case Constants.PAY_WAY_WX:
            case Constants.PAY_WAY_UNIPAY:

                break;

        }

    }


    private void payBat(int type) {
        int amt = CommonFunc.recoveryMemberInfo(this).getRealMoney();

        switch (type) {
            case Constants.PAY_WAY_ALY:
                CommonFunc.pay(this, 12, "660000", StringUtils.formatIntMoney(amt), clientNo);
                break;
            case Constants.PAY_WAY_WX:
                CommonFunc.pay(this, 11, "660000", StringUtils.formatIntMoney(amt), clientNo);
                break;
            case Constants.PAY_WAY_UNIPAY:
//                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_UNIPAY);
                break;
        }


    }





    /**
     * 刷卡
     */
    private void payflot1() {

        int amt = CommonFunc.recoveryMemberInfo(this).getRealMoney();
        CommonFunc.pay(this, 0, "000000", StringUtils.formatIntMoney(amt), clientNo);

    }


    /**
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout);
        //设置流水上送参数
        TransUploadRequest request = CommonFunc.setTransUploadData(mContext, printerData, CommonFunc.recoveryMemberInfo(this),
                clientNo, "", ""
        );

        //打印订单号与流水上送统一
        printerData.setClientOrderNo(request.getClientOrderNo());

        //流水上送
        transUploadAction1(request);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {

            switch (resultCode) {
                case Activity.RESULT_OK:

                    LogUtils.e("msg_tp:" + data.getStringExtra("msg_tp") + "\n" +
                            "pay_tp:" + data.getStringExtra("pay_tp") + "\n" +
                            "proc_tp:" + data.getStringExtra("proc_tp") + "\n" +
                            "proc_cd:" + data.getStringExtra("proc_cd") + "\n" +
                            "amt:" + data.getStringExtra("amt") + "\n" +
                            "systraceno:" + data.getStringExtra("systraceno") + "\n" +
                            "sysoldtraceno:" + data.getStringExtra("sysoldtraceno") + "\n" +
                            "order_no:" + data.getStringExtra("order_no") + "\n" +
                            "appid:" + data.getStringExtra("appid") + "\n" +
                            "time_stamp:" + data.getStringExtra("time_stamp") + "\n" +
                            "print_info:" + data.getStringExtra("print_info") + "\n" +
                            "batchbillno:" + data.getStringExtra("batchbillno") + "\n" +
                            "merid:" + data.getStringExtra("merid") + "\n" +
                            "termid:" + data.getStringExtra("termid") + "\n");

//                    LogUtils.e("txndetail:" + data.getExtras().getString("txndetail"));

                    ALog.json("txndetail", data.getExtras().getString("txndetail"));

                    String payType = data.getStringExtra("pay_tp");
                    String msg_tp = data.getStringExtra("msg_tp");
                    String detail = data.getExtras().getString("txndetail");
                    //根据交易应答类型来区分
                    if ("0210".equals(msg_tp)) {
                        if ("0".equals(payType)) { //银行卡
                            setFlot(detail);
                        } else if ("1".equals(payType)) { //微信
                            setSmPay(detail, Constants.PAY_WAY_WX);
                        } else if ("2".equals(payType)) { //支付宝
                            setSmPay(detail, Constants.PAY_WAY_ALY);
                        }
                    }else if ("0310".equals(msg_tp)){
                        Gson gson = new Gson();
                        TransUploadRequest request = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                        ALog.json(request.toString());
                        getPrinterData(request);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    ToastUtils.CustomShow(mContext, data.getStringExtra("reason"));
                    break;
                case -2:
                    ToastUtils.CustomShow(mContext, data.getStringExtra("reason"));
                    break;
                default:
                    break;
            }
        }else {

            if (resultCode != RESULT_OK) {
                return;
            }
            switch (requestCode) {
                case REQUEST_CASH:
                    int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");
                    payCash1(oddChangeAmout);
                    break;
                case REQUEST_CAPTURE_QB:
//                    String result_qb = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
//                    LogUtils.e("result", result_qb);
//                    ZfQBPay2(result_qb, Constants.PAY_WAY_QB);

                    break;
                default:
                    break;
            }
        }
    }



    /**
     * 钱包末笔查询
     */
    private void ZfQbQuery() {
        CommonFunc.ZfQbFailQuery(this, new ActionCallbackListener<TransUploadResponse>() {
            @Override
            public void onSuccess(TransUploadResponse data) {

                FailureData failureData = CommonFunc.recoveryFailureInfo(ZfPayActivity.this);
                //流水上送
                setStkPay(failureData.getOrderNo(), failureData.getTime(), failureData.getTraceNum(), failureData.getPay_type());
                final LoadingDialog dialog = new LoadingDialog(mContext);
                dialog.show("正在查询...");
                dialog.setCancelable(false);
                setTransUpdateResponse(data, dialog, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {
                ToastUtils.CustomShow(ZfPayActivity.this, s + "#" + error_msg);
            }

            @Override
            public void onLogin() {

            }
        });
    }



    /**
     * 设置钱包异常查询
     *
     * @param orderNo
     * @param time
     * @param payWayQb
     */
    private void setQbFailureQuery(String orderNo, String time, String traceNum, int payWayQb, String cardNo) {
        FailureData data = new FailureData();
        data.setPay_type(payWayQb);
        data.setOrderNo(orderNo);
        data.setTime(time);
        data.setTraceNum(traceNum);
        data.setStatus(true);
        data.setCardNo(cardNo);
        CommonFunc.setBackFailureInfo(this, data);
    }






    private void setCashPrintData1(int oddChangeAmout) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(ToolNewLand.getToolNewLand().getSerialNo());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getTradeMoney()));
        printerData.setReceiveAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_CASH);

    }




    private void setFlot(String data){

        Gson gson = new Gson();
        SmResponse smResponse = gson.fromJson(data, SmResponse.class);

        printerData.setMerchantName(smResponse.getMername());
        printerData.setMerchantNo(smResponse.getMerid());//(transInfo.getMid());
        printerData.setTerminalId(smResponse.getTermid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setAcquirer(smResponse.getAcqno());
        printerData.setIssuer(smResponse.getIison());
        printerData.setCardNo(smResponse.getPriaccount());
        printerData.setTransType(1 + "");
        printerData.setExpDate(smResponse.getExpdate());
        printerData.setBatchNO(smResponse.getBatchno());
        printerData.setVoucherNo(smResponse.getSystraceno());
        printerData.setDateTime(
                StringUtils.formatTime(smResponse.getTranslocaldate()+smResponse.getTranslocaltime()));
        printerData.setAuthNo(smResponse.getAuthcode());
        printerData.setReferNo(smResponse.getRefernumber());
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(smResponse.getTransamount());
        printerData.setPayType(Constants.PAY_WAY_FLOT);


        //设置流水上送需要上送的参数
        TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData,
                CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
                clientNo,
                printerData.getVoucherNo(), printerData.getReferNo());

        //打印的订单号与流水上送的统一
        printerData.setClientOrderNo(request.getClientOrderNo());

        //流水上送
        transUploadAction1(request);
    }



    private void setSmPay(String data, int type){

        Gson gson = new Gson();
        SmResponse smResponse = gson.fromJson(data, SmResponse.class);



        printerData.setMerchantName(smResponse.getMername());
        printerData.setMerchantNo(smResponse.getMerid());
        printerData.setTerminalId(smResponse.getTermid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(smResponse.getSystraceno());
        printerData.setAuthCode(smResponse.getOrderid_scan());
        printerData.setDateTime(StringUtils.formatTime(smResponse.getTranslocaldate()+smResponse.getTranslocaltime()));
        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
        printerData.setAmount(smResponse.getTransamount());
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());

        printerData.setPayType(type);


        TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData, CommonFunc.recoveryMemberInfo(this),
                clientNo, printerData.getTransNo(), printerData.getAuthCode()
        );
        printerData.setClientOrderNo(request.getClientOrderNo());
        transUploadAction1(request);

    }




    /**
     * 保存数据
     */
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


    private void getPrinterData(final TransUploadRequest request) {

        final LoadingDialog dialog = new LoadingDialog(this);
        dialog.show("获取打印信息...");
        this.sbsAction.getPrinterData(this, request.getSid(), request.getClientOrderNo(), new ActionCallbackListener<TransUploadResponse>() {

            @Override
            public void onSuccess(TransUploadResponse data) {
                setTransUpdateResponse(data, dialog, false);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                dialog.dismiss();
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }


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
                ToastUtils.CustomShow(ZfPayActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, ZfPayActivity.this);
                ToolNewLand.getToolNewLand().printText(printerData);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }











    private void showLayout() {
        ll_payType.setVisibility(View.GONE);
        ll_payFinish.setVisibility(View.VISIBLE);
    }

    /**
     * 将流水上送的数据转成字串保存在打印的对象中
     * 不管成功失败，流水上送的数据保存下来
     *
     * @param request
     */
    private void setTransUpLoadData(TransUploadRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
//        LogUtils.e(data);
        ALog.json(data);
        printerData.setTransUploadData(data);
    }


    private void setStkRequestData(StkPayRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
//        LogUtils.e(data);
        ALog.json(data);
        printerData.setStkRequestData(data);
    }

    private void setCounponData(List<Couponsn> data) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String counponStr = gson.toJson(data);
        printerData.setCouponData(counponStr);
    }


    /**
     * 用来返回主线程 打印小票
     */
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
            Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
            printerData.setPoint_bitmap(point_bitmap);
            printerData.setCoupon_bitmap(title_bitmap);

            showLayout();

            // 打印
//            Printer.getInstance(ZfPayActivity.this).print(printerData, ZfPayActivity.this);
            ToolNewLand.getToolNewLand().printText(printerData);


        }


    };

    protected void setTransUpdateResponse(final TransUploadResponse data, final LoadingDialog dialog, boolean flag) {
        printerData.setPoint_url(data.getPoint_url());
        printerData.setPoint(data.getPoint());
        printerData.setPointCurrent(data.getPointCurrent());
        printerData.setCoupon(data.getCoupon_url());
        setCounponData(data.getCoupon());
        printerData.setBackAmt(data.getBackAmt());
        printerData.setApp_type(app_type);
        if (flag) {
            // 保存打印的数据，不保存图片数据
            PrinterDataSave();
        }

        //开启线程下载二维码图片
        new Thread(new Runnable() {

            @Override
            public void run() {

                Bitmap point_bitmap = null;
                Bitmap title_bitmap = null;
                if (!StringUtils.isEmpty(data.getPoint_url())) {
                    try {
                        point_bitmap = Glide.with(getApplicationContext())
                                .load(data.getPoint_url())
                                .asBitmap()
                                .centerCrop()
                                .into(Constants.er_width, Constants.er_height).get();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                if (!StringUtils.isEmpty(data.getCoupon_url())) {

                    try {
                        title_bitmap = Glide.with(getApplicationContext())
                                .load(data.getCoupon_url())
                                .asBitmap()
                                .centerCrop()
                                .into(Constants.er_width, Constants.er_height).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }


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

}

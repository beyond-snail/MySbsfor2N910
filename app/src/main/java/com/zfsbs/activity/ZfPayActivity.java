package com.zfsbs.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myokhttp.MyOkHttp;
import com.myokhttp.response.JsonResponseHandler;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.dialog.PassWordDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.view.MyGridView;
import com.wosai.upay.bean.UpayResult;
import com.yzq.testzxing.zxing.android.CaptureActivity;
import com.zfsbs.R;
import com.zfsbs.adapter.MyPayTypeAdapter;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.config.EnumConstsSbs;
import com.zfsbs.core.action.BATPay;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.action.ZfQbAction;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.core.myinterface.BatInterface;
import com.zfsbs.model.ActivateApiResponse;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.LoginApiResponse;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.PayType;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.model.SmResponse;
import com.zfsbs.model.StkPayRequest;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.model.ZfQbResponse;
import com.zfsbs.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.wosai.upay.common.UpayTask.context;
import static com.zfsbs.R.string.pay_type;
import static com.zfsbs.common.CommonFunc.getNewClientSn;
import static com.zfsbs.common.CommonFunc.startAction;
import static com.zfsbs.common.CommonFunc.startResultAction;
import static com.zfsbs.config.Constants.PAY_FY_ALY;
import static com.zfsbs.config.Constants.PAY_FY_UNION;
import static com.zfsbs.config.Constants.PAY_FY_WX;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_ALY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_QB;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_UNIPAY;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_WX;
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
//        initData();
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

//        if (!Constants.isUsedQb || app_type == Config.APP_Richer_e) {
//            btnQb.setVisibility(View.INVISIBLE);
//            btnCash.setVisibility(View.INVISIBLE);
//        }
//
//        if (app_type == Config.APP_HD) {
//            ll_pointAmount.setVisibility(View.INVISIBLE);
//            ll_couponAmount.setVisibility(View.INVISIBLE);
//            btnQb.setVisibility(View.INVISIBLE);
//        } else if (app_type == Config.APP_YXF) {
//            btnQb.setVisibility(View.INVISIBLE);
//        }


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
                        startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_QB);
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


//        bat = new BATPay(this);
        fybat = new FyBat(this, listener1);
        qbpay = new ZfQbAction(this);

        if (!StringUtils.isEmpty(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo())) {
            printerData.setPhoneNo(CommonFunc.recoveryMemberInfo(ZfPayActivity.this).getMemberCardNo());
        }


    }


    private FyBat.FYPayResultEvent listener1 = new FyBat.FYPayResultEvent() {
        @Override
        public void onSuccess(FyMicropayResponse data) {

            setFySmPay1(data);
        }

        @Override
        public void onSuccess(FyQueryResponse data) {
            //先判断本地数据是否存在，防止从华尔街平台拿到的是上一笔成功的交易
            SbsPrinterData datas = DataSupport.findLast(SbsPrinterData.class);
//            LogUtils.e("AuthCode",datas.getAuthCode());
//            LogUtils.e("getMchnt_order_no",data.getMchnt_order_no());
            if (!StringUtils.isEmpty(datas.getAuthCode()) && datas.getAuthCode().equals(data.getMchnt_order_no())) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请确认消费者交易成功。");
                return;
            }
//            setFySmPayQurey1(data);
        }

        @Override
        public void onSuccess(FyRefundResponse data) {

        }

        @Override
        public void onFailure(int statusCode, String error_msg, String type, String query_amount) {
            showLayoutEndQuery();
        }

        @Override
        public void onFailure(FyMicropayRequest data) {
            showLayoutEndQuery();
            if (data.getType().equals(Constants.PAY_FY_ALY)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(Constants.PAY_FY_WX)) {
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_PAY);
            } else if (data.getType().equals(Constants.PAY_FY_UNION)){
                setFyPayFailureQuery(data.getOutOrderNum(), data.getAmount() + "", data.getType(), true, Constants.PAY_WAY_UNIPAY, Constants.FY_FAILURE_PAY);
            }

        }

        @Override
        public void onFailure(FyQueryRequest data) {
            showLayoutEndQuery();
            if (data == null) {
                ToastUtils.CustomShow(ZfPayActivity.this, "请求数据为空，无法查询末笔");
                return;
            }
            if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_ALY, Constants.FY_FAILURE_QUERY);
            } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_WX, Constants.FY_FAILURE_QUERY);
            }else if (data.getOrder_type().equals(Constants.PAY_FY_UNION)) {
                setFyQueryFailureQuery(data.getOutOrderNum(), data.getOrder_type(), data.getMchnt_order_no(), true, Constants.PAY_WAY_UNIPAY, Constants.FY_FAILURE_QUERY);
            }

        }


        @Override
        public void onLogin() {
            AppManager.getAppManager().finishAllActivity();
            if (Config.OPERATOR_UI_BEFORE) {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
            } else {
                CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
            }
        }
    };


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
                if (app_type == Config.APP_SBS) {
                    if (printerData.getPayType() == Constants.PAY_WAY_STK || printerData.getPayType() == Constants.PAY_WAY_QB){
//                        Printer.print(printerData, ZfPayActivity.this);
                    }else {
                        Gson gson = new Gson();
                        TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                        LogUtils.e(data.toString());
                        getPrinterData(data);//(printerData.getRequest());
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
        request.setActivateCode(CommonFunc.getSerialNo(mContext));
        request.setOrderNo(clientNo);
        request.setPayPassword(psw);
        request.setSerialNum(CommonFunc.getSerialNo(mContext));
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

        request.setActivateCode(CommonFunc.getSerialNo(mContext));
        request.setOrderNo(clientNo);
        request.setQrCode(temp[0]);
        request.setSerialNum(CommonFunc.getSerialNo(mContext));
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
        printerData.setTerminalId(CommonFunc.getSerialNo(mContext));
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

        if (app_type == Config.APP_SBS) {

            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData, CommonFunc.recoveryMemberInfo(this),
                    clientNo, "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);
        }

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
                if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_PAY) {
                    ZfFyPayQuery();
                } else if (CommonFunc.recoveryFailureInfo(this).getFaiureType() == Constants.FY_FAILURE_QUERY) {
                    ZfFyQuery();
                }
                break;

        }

    }


    private void payBat(int type) {
        String sm_type = MyApplication.getInstance().getLoginData().getScanPayType();
        int amt = CommonFunc.recoveryMemberInfo(this).getRealMoney();

        //富友扫码
        if (!StringUtils.isEmpty(sm_type) && StringUtils.isEquals(sm_type, Constants.SM_TYPE_FY)) {

            switch (type) {
                case Constants.PAY_WAY_ALY:
//                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);

                    CommonFunc.pay(this, 12, "660000", StringUtils.formatIntMoney(amt), clientNo);
                    break;
                case Constants.PAY_WAY_WX:
//                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_WX);
                    CommonFunc.pay(this, 11, "660000", StringUtils.formatIntMoney(amt), clientNo);
                    break;
                case Constants.PAY_WAY_UNIPAY:
//                    startResultAction(ZfPayActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_UNIPAY);
                    break;
            }

            return;
        }

    }





    /**
     * 刷卡
     */
    private void payflot1() {

        int amt = CommonFunc.recoveryMemberInfo(this).getRealMoney();


        CommonFunc.pay(this, 0, "000000", StringUtils.formatIntMoney(amt), clientNo);


//        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
//        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
//        PayCommon.sale(this, CommonFunc.recoveryMemberInfo(this).getRealMoney(), mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
//            @Override
//            public void success(ComTransInfo transInfo) {
//                //设置打印的信息
//                setFlotPrintData1(transInfo);
//
//                //判断是否是商博士
//                if (app_type == Config.APP_SBS) {
//
//                    //设置流水上送需要上送的参数
//                    TransUploadRequest request = CommonFunc.setTransUploadData(printerData,
//                            CommonFunc.recoveryMemberInfo(ZfPayActivity.this),
//                            clientNo,
//                            printerData.getVoucherNo(), printerData.getReferNo());
//
//                    //打印的订单号与流水上送的统一
//                    printerData.setClientOrderNo(request.getClientOrderNo());
//
//                    //流水上送
//                    transUploadAction1(request);
//                }
//            }
//
//            @Override
//            public void failed(String error) {
////                ToastUtils.CustomShow(ZfPayActivity.this, error);
//                final CommonDialog confirmDialog = new CommonDialog(ZfPayActivity.this, error);
//                confirmDialog.show();
//                confirmDialog.setClicklistener(new CommonDialog.ClickListenerInterface() {
//                    @Override
//                    public void doConfirm() {
//
//                    }
//                });
//            }
//        });


    }


    /**
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout);

        if (app_type == Config.APP_SBS) {
            //设置流水上送参数
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext, printerData, CommonFunc.recoveryMemberInfo(this),
                    clientNo, "", ""
            );

            //打印订单号与流水上送统一
            printerData.setClientOrderNo(request.getClientOrderNo());

            //流水上送
            transUploadAction1(request);
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && data != null) {

            switch (resultCode) {
                case Activity.RESULT_OK:

                    String payType = data.getStringExtra("proc_tp");
                    String detail = data.getExtras().getString("txndetail");
                    if ("0".equals(payType)){ //银行卡
                        setFlot(detail);
                    } else if ("1".equals(payType)){ //微信
                        setSmPay(detail, Constants.PAY_WAY_WX);
                    } else if ("2".equals(payType)){ //支付宝
                        setSmPay(detail, Constants.PAY_WAY_ALY);
                    }
                    break;
                case Activity.RESULT_CANCELED:
//                    mTvResult.setText("reason:" + data.getStringExtra("reason"));
                    ToastUtils.CustomShow(mContext, data.getStringExtra("reason"));
                    break;
                case -2:
//                    mTvResult.setText("reason" + data.getStringExtra("reason"));
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
//                case REQUEST_CAPTURE_WX:
//                    String result_wx = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
//                    LogUtils.e("result", result_wx);
//                    FyWxPay1(result_wx);
//                    break;
//                case REQUEST_CAPTURE_ALY:
//                    String result_aly = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
//                    LogUtils.e("result", result_aly);
//                    FyAlyPay1(result_aly);
//                    break;
//                case REQUEST_CAPTURE_UNIPAY:
//                    String result_uni = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
//                    LogUtils.e("result", result_uni);
//                    FyUnionPay1(result_uni);
//                    break;
                case REQUEST_CAPTURE_QB:
                    String result_qb = data.getExtras().getString(CaptureActivity.SCAN_RESULT);
                    LogUtils.e("result", result_qb);
                    ZfQBPay2(result_qb, Constants.PAY_WAY_QB);

                    break;
                default:
                    break;
            }
        }
    }


    private void FyWxPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_WX);
        printerData.setClientOrderNo(clientNo);
        fybat.pay1(code, PAY_FY_WX, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }


    private void FyAlyPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_ALY);
        printerData.setClientOrderNo(clientNo);
        fybat.pay1(code, PAY_FY_ALY, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
    }

    private void FyUnionPay1(String code) {
        printerData.setPayType(Constants.PAY_WAY_UNIPAY);
        printerData.setClientOrderNo(clientNo);
        fybat.pay1(code, PAY_FY_UNION, printerData.getClientOrderNo(), CommonFunc.recoveryMemberInfo(this).getRealMoney());
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
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }


    /**
     * 富友扫码支付异常处理
     */
    private void ZfFyPayQuery() {
        fybat.terminalQuery1(CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getAmount(), true,
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
    }

    /**
     * 富友扫码查询异常处理
     */
    private void ZfFyQuery() {

        fybat.query1(this, CommonFunc.recoveryFailureInfo(this).getOrder_type(), CommonFunc.recoveryFailureInfo(this).getOrderNo(),
                CommonFunc.recoveryFailureInfo(this).getOutOrderNo());
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

    /**
     * 设置富友SM异常查询
     *
     * @param amount
     * @param type
     * @param isStatus
     */
    private void setFyPayFailureQuery(String outOrderNum, String amount, String type, boolean isStatus, int payWay, int failureType) {

        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);

        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setAmount(amount);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);

    }

    /**
     * 设置富友查询异常查询数据
     *
     * @param type
     * @param order_no
     * @param isStatus
     * @param payWay
     */
    private void setFyQueryFailureQuery(String outOrderNum, String type, String order_no, boolean isStatus, int payWay, int failureType) {
        boolean isMember = (boolean) SPUtils.get(this, Config.isHdMember, false);
        FailureData data = new FailureData();
        data.setOutOrderNo(outOrderNum);
        data.setOrder_type(type);
        data.setStatus(isStatus);
        data.setPay_type(payWay);
        data.setOrderNo(order_no);
        data.setFaiureType(failureType);
        data.setApp_type(app_type);
        data.setMember(isMember);
        CommonFunc.setBackFailureInfo(this, data);
    }






    private void setCashPrintData1(int oddChangeAmout) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(CommonFunc.getSerialNo(mContext));
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getTradeMoney()));
        printerData.setReceiveAmount(StringUtils.formatIntMoney(CommonFunc.recoveryMemberInfo(this).getRealMoney()));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
        printerData.setPayType(Constants.PAY_WAY_CASH);

    }


//    protected void setFlotPrintData1(ComTransInfo transInfo) {
//        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
//        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());//(transInfo.getMid());
//        printerData.setTerminalId(transInfo.getTid());
//        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
//        printerData.setAcquirer(transInfo.getAcquirerCode());
//        printerData.setIssuer(transInfo.getIssuerCode());
//        printerData.setCardNo(transInfo.getPan());
//        printerData.setTransType(transInfo.getTransType() + "");
//        printerData.setExpDate(transInfo.getExpiryDate());
//        printerData.setBatchNO(StringUtils.fillZero(transInfo.getBatchNumber() + "", 6));
//        printerData.setVoucherNo(StringUtils.fillZero(transInfo.getTrace() + "", 6));
//        printerData.setDateTime(
//                StringUtils.formatTime(StringUtils.getCurYear() + transInfo.getTransDate() + transInfo.getTransTime()));
//        printerData.setAuthNo(transInfo.getAuthCode());
//        printerData.setReferNo(transInfo.getRrn());
//        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
//        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
//        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
//        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
//        printerData.setPayType(Constants.PAY_WAY_FLOT);
//    }

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
        } else if (data.getOrder_type().equals(Constants.PAY_FY_UNION)) {
            printerData.setPayType(Constants.PAY_WAY_UNIPAY);
        }

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData, CommonFunc.recoveryMemberInfo(this),
                    clientNo, printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        }

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

        if (app_type == Config.APP_SBS) {
            TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData, CommonFunc.recoveryMemberInfo(this),
                    clientNo, printerData.getTransNo(), printerData.getAuthCode()
            );
            printerData.setClientOrderNo(request.getClientOrderNo());
            transUploadAction1(request);
        }
    }


    private void setFySmPayQurey1(String data) {

//        Gson gson = new Gson();
//        SmResponse smResponse = gson.fromJson(data, SmResponse.class);



//        printerData.setMerchantName(smResponse.getMername());
//        printerData.setMerchantNo(smResponse.getMerid());
//        printerData.setTerminalId(smResponse.getTermid());
//        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
//        printerData.setTransNo(smResponse.getSystraceno());
//        printerData.setAuthCode(smResponse.getOrderid_scan());
//        printerData.setDateTime(StringUtils.formatTime(smResponse.getTranslocaldate()+smResponse.getTranslocaltime()));
//        printerData.setOrderAmount(CommonFunc.recoveryMemberInfo(this).getTradeMoney());
//        printerData.setAmount(smResponse.getTransamount());
//        printerData.setPointCoverMoney(CommonFunc.recoveryMemberInfo(this).getPointCoverMoney());
//        printerData.setCouponCoverMoney(CommonFunc.recoveryMemberInfo(this).getCouponCoverMoney());
//        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());
//        if (data.getOrder_type().equals(Constants.PAY_FY_ALY)) {
//            printerData.setPayType(Constants.PAY_WAY_ALY);
//        } else if (data.getOrder_type().equals(Constants.PAY_FY_WX)) {
//            printerData.setPayType(Constants.PAY_WAY_WX);
//        } else if (data.getOrder_type().equals(Constants.PAY_FY_UNION)) {
//            printerData.setPayType(Constants.PAY_WAY_UNIPAY);
//        }
//
//        printerData.setPayType(type);
//
//        if (app_type == Config.APP_SBS) {
//            TransUploadRequest request = CommonFunc.setTransUploadData(printerData, CommonFunc.recoveryMemberInfo(this),
//                    clientNo, printerData.getTransNo(), printerData.getAuthCode()
//            );
//            printerData.setClientOrderNo(request.getClientOrderNo());
//            transUploadAction1(request);
//        }

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
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
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
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                dialog.dismiss();
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(ZfPayActivity.this, OperatorLoginActivity1.class, false);
                }
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
                                .into(200, 200).get();

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
                                .into(200, 200).get();
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

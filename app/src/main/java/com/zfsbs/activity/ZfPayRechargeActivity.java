package com.zfsbs.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.model.SbsPrinterData;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.FyBat;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ChargeBlance;
import com.zfsbs.model.FailureData;
import com.zfsbs.model.FyMicropayRequest;
import com.zfsbs.model.FyMicropayResponse;
import com.zfsbs.model.FyQueryRequest;
import com.zfsbs.model.FyQueryResponse;
import com.zfsbs.model.FyRefundResponse;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.SmResponse;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import static com.zfsbs.config.Constants.PAY_FY_ALY;
import static com.zfsbs.config.Constants.PAY_FY_UNION;
import static com.zfsbs.config.Constants.PAY_FY_WX;
import static com.zfsbs.config.Constants.REQUEST_CAPTURE_UNIPAY;
import static com.zfsbs.config.Constants.REQUEST_CASH;
import static com.zfsbs.config.Constants.REQUEST_flot_CASH;


public class ZfPayRechargeActivity extends BaseActivity implements View.OnClickListener {

//    private FyBat fybat;

//    private RechargeAmount vo;
    private String cardNo;
    private String tgy;
//    private String cardId;
    private String oldAmount;
    private String actualAmount;
    private String orderNo;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_no_pay_amount;
    private LinearLayout ll_payQuery;

    private TextView orderAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_recharge_pay);
//        AppManager.getAppManager().addActivity(this);
        initTitle("选择充值方式");

        initData();
        initView();

    }

    private void initData() {

        cardNo = getIntent().getBundleExtra("data").getString("cardNo");
        tgy = getIntent().getBundleExtra("data").getString("tgy");
        oldAmount = getIntent().getBundleExtra("data").getString("oldAmount");
        actualAmount = getIntent().getBundleExtra("data").getString("actualAmount");
        orderNo = getIntent().getBundleExtra("data").getString("orderNo");

//        cardId = getIntent().getBundleExtra("data").getString("card_id");
//        vo = (RechargeAmount) getIntent().getBundleExtra("data").getSerializable("RechargeAmount");
//        if (vo == null){
//            ToastUtils.CustomShow(mContext, "数据有误");
//            onBackPressed();
//            return;
//        }

//        fybat = new FyBat(this, listener1);
    }

    private void initView() {

        orderAmount = (TextView) findViewById(R.id.id_order_amount);
        orderAmount.setText(StringUtils.formatStrMoney(oldAmount));

        textView(R.id.id_dz_amount).setText(StringUtils.formatStrMoney(actualAmount));
        TextView etCardNo = textView(R.id.id_memberCardNo);
        etCardNo.setText(cardNo);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);
        ll_no_pay_amount = (LinearLayout) findViewById(R.id.ll_no_pay_amount);

        linearLayout(R.id.pay_wx).setOnClickListener(this);
        linearLayout(R.id.pay_aly).setOnClickListener(this);
        linearLayout(R.id.pay_cash).setOnClickListener(this);
        linearLayout(R.id.id_pay_flot).setOnClickListener(this);
        linearLayout(R.id.id_ll_unionpay).setOnClickListener(this);

        linearLayout(R.id.id_ll_unionpay).setVisibility(View.INVISIBLE);

        button(R.id.id_print).setOnClickListener(this);
        button(R.id.id_finish).setOnClickListener(this);
        button(R.id.id_query).setOnClickListener(this);
        button(R.id.id_terminal_query_sure).setOnClickListener(this);

    }






    private void showLayoutEndQuery() {
        ll_payType.setVisibility(View.GONE);
        ll_payQuery.setVisibility(View.VISIBLE);
    }

    private void showLayout() {
        ll_payType.setVisibility(View.GONE);
        ll_payFinish.setVisibility(View.VISIBLE);
    }




    private void setCashPrintData1(int oddChangeAmout, int payType) {
        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
        printerData.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        printerData.setTerminalId(ToolNewLand.getToolNewLand().getSerialNo());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setDateTime(StringUtils.getCurTime());
        printerData.setAmount(StringUtils.formatStrMoney(oldAmount));
        printerData.setReceiveAmount(StringUtils.formatStrMoney(actualAmount));
        printerData.setOddChangeAmout(StringUtils.formatIntMoney(oddChangeAmout));
        printerData.setPayType(payType);

    }

    /**
     * 现金
     *
     * @param oddChangeAmout
     */
    private void payCash1(int oddChangeAmout, int payType) {

        //设置打印信息
        setCashPrintData1(oddChangeAmout, payType);

        //打印订单号与流水上送统一
        printerData.setClientOrderNo(orderNo);

        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();
        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getTerminalNo());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));
        rechargeUpLoad.setPayType(Constants.PAY_WAY_CASH);
        rechargeUpLoad.setPromotion_num(tgy);

        rechargeUpload(rechargeUpLoad);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_ll_unionpay:
//                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_UNIPAY);
                break;
            case R.id.pay_wx:
//                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_WX);
                CommonFunc.pay(this, 11, "660000", StringUtils.formatStrMoney(oldAmount), orderNo);
                break;
            case R.id.pay_aly:
//                CommonFunc.startResultAction(ZfPayRechargeActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE_ALY);
                CommonFunc.pay(this, 12, "660000", StringUtils.formatStrMoney(oldAmount), orderNo);
                break;
            case R.id.pay_cash: {
                Bundle bundle = new Bundle();
                bundle.putString("amount", orderAmount.getText().toString());
                CommonFunc.startResultAction(this, ZfPayCashActivity.class, bundle, REQUEST_CASH);
            }
                break;
            case R.id.id_pay_flot:
                payflot1();
                break;
            case R.id.id_print:
//                Printer.print(printerData, ZfPayRechargeActivity.this);
                ToolNewLand.getToolNewLand().printText(printerData);
                break;

            case R.id.id_query:
//                setLastQuerySend1();
                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure:
                finish();
                break;
            default:
                break;
        }
    }



    /**
     * 刷卡
     */
    private void payflot1() {
        CommonFunc.pay(this, 0, "000000", StringUtils.formatStrMoney(oldAmount), orderNo);
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
                    String detail = data.getExtras().getString("txndetail");
                    if ("0".equals(payType)){ //银行卡
                        setFlot(detail);
                    } else if ("1".equals(payType)){ //微信
                        setSmPay(detail, Constants.PAY_WAY_RECHARGE_WX,  Constants.PAY_WAY_WX);
                    } else if ("2".equals(payType)){ //支付宝
                        setSmPay(detail, Constants.PAY_WAY_RECHARGE_ALY, Constants.PAY_WAY_ALY);
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
                case REQUEST_flot_CASH: {
                    int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                    payCash1(oddChangeAmout, Constants.PAY_WAY_PAY_FLOT);
                }
                break;
                case REQUEST_CASH: {
                    int oddChangeAmout = data.getBundleExtra("bundle").getInt("oddChangeAmout");

                    payCash1(oddChangeAmout, Constants.PAY_WAY_RECHARGE_CASH);
                }
                default:
                    break;
            }
        }
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
        printerData.setOrderAmount(Integer.parseInt(actualAmount));
        printerData.setAmount(smResponse.getTransamount());
        printerData.setPayType(Constants.PAY_WAY_RECHARGE_FLOT);


        printerData.setClientOrderNo(orderNo);


        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();
        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getActiveCode());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));


        rechargeUpLoad.setPayType(Constants.PAY_WAY_FLOT);

        rechargeUpLoad.setPromotion_num(tgy);
        rechargeUpLoad.setTransNo(printerData.getTransNo());
        rechargeUpLoad.setAuthCode(printerData.getAuthCode());


        rechargeUpload(rechargeUpLoad);
    }


    private void setSmPay(String data, int payType, int UploadType){

        Gson gson = new Gson();
        SmResponse smResponse = gson.fromJson(data, SmResponse.class);



        printerData.setMerchantName(smResponse.getMername());
        printerData.setMerchantNo(smResponse.getMerid());
        printerData.setTerminalId(smResponse.getTermid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setTransNo(smResponse.getSystraceno());
        printerData.setAuthCode(smResponse.getOrderid_scan());
        printerData.setDateTime(StringUtils.formatTime(smResponse.getTranslocaldate()+smResponse.getTranslocaltime()));
        printerData.setOrderAmount(Integer.parseInt(actualAmount));
        printerData.setAmount(smResponse.getTransamount());
        printerData.setScanPayType(MyApplication.getInstance().getLoginData().getScanPayType());

        printerData.setPayType(payType);

        printerData.setClientOrderNo(orderNo);

        //流水上送
        RechargeUpLoad rechargeUpLoad = new RechargeUpLoad();

        rechargeUpLoad.setSid(MyApplication.getInstance().getLoginData().getSid());
        rechargeUpLoad.setPayAmount(Integer.parseInt(oldAmount));
        rechargeUpLoad.setOrderNo(printerData.getClientOrderNo());
        rechargeUpLoad.setActivateCode(MyApplication.getInstance().getLoginData().getActiveCode());
        rechargeUpLoad.setMerchantNo(MyApplication.getInstance().getLoginData().getFyMerchantNo());
        rechargeUpLoad.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));
        rechargeUpLoad.setSerialNum(ToolNewLand.getToolNewLand().getSerialNo());
        rechargeUpLoad.setPayType(printerData.getPayType());
        rechargeUpLoad.setOperator_num((String) SPUtils.get(mContext, Constants.USER_NAME, ""));

        //这个地方支付与充值传的是一样

        rechargeUpLoad.setPayType(UploadType);

        rechargeUpLoad.setPromotion_num(tgy);
        rechargeUpLoad.setTransNo(printerData.getTransNo());
        rechargeUpLoad.setAuthCode(printerData.getAuthCode());

        rechargeUpload(rechargeUpLoad);
    }












    /**
     * 将流水上送的数据转成字串保存在打印的对象中
     * 不管成功失败，流水上送的数据保存下来
     *
     * @param request
     */
    private void setRechargeUpLoadData(RechargeUpLoad request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
        ALog.json(data);
        printerData.setRechargeUpload(data);
    }


    private void rechargeUpload(final RechargeUpLoad rechargeUpLoad){
        sbsAction.rechargePay(mContext, rechargeUpLoad, new ActionCallbackListener<ChargeBlance>() {
            @Override
            public void onSuccess(ChargeBlance data) {
                setRechargeUpLoadData(rechargeUpLoad);
                printerData.setPromotion_num(rechargeUpLoad.getPromotion_num());
                printerData.setPacektRemian(data.getPacektRemian());
                printerData.setRealize_card_num(data.getRealize_card_num());
                printerData.setMember_name(data.getMember_name());
                PrinterDataSave();

                showLayout();

                // 打印
                ToolNewLand.getToolNewLand().printText(printerData);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(ZfPayRechargeActivity.this, errorEvent + "#" + message);
                showLayout();
                setRechargeUpLoadData(rechargeUpLoad);
                printerData.setUploadFlag(true);
                printerData.setApp_type(0);
                // 保存打印的数据，不保存图片数据
                PrinterDataSave();
                // 打印
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
}

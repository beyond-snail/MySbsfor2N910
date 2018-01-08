package com.zfsbs.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.SbsPrinterData;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.ALog;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.Couponsn;
import com.zfsbs.model.SmResponse;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;
import com.zfsbs.myapplication.MyApplication;

import org.litepal.crud.DataSupport;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.zfsbs.common.CommonFunc.startAction;

public class ZfPayPreauthActivity extends BaseActivity implements OnClickListener {

    private static final String TAG = "ZfPayActivity";

    private TextView tOrderAmount;
    private TextView tPayAmount;
    private TextView tPayPointAmount;
    private TextView tPayCouponAmount;

//    private LinearLayout btnPayflot;
//    private LinearLayout btnCash;
//    private LinearLayout btnAly;
//    private LinearLayout btnWx;
//    private LinearLayout btnQb;


    private LinearLayout btnPreauth;
    private LinearLayout btnAuthCancel;
    private LinearLayout btnAuthComplete;
    private LinearLayout btnVoidAuthComplete;



    private Button btnPrint;
    private Button btnPrintfinish;
    private Button btnNopayAmount;
    private Button btnQuery;
    private Button btnQueryEnd;

    private LinearLayout ll_payType;
    private LinearLayout ll_payFinish;
    private LinearLayout ll_payQuery;

    private List<SbsPrinterData> allData;
    private String orderNo;


    private int amount;


    private int app_type = 0;

    private int currentPayType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_pay_preauth_type);
        initTitle("预授权");

        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);

        amount = getIntent().getIntExtra("amount", 0);


        initView();
        addListenster();

    }

    private void initView() {


        tOrderAmount = (TextView) findViewById(R.id.id_orderAmount);
        tPayAmount = (TextView) findViewById(R.id.id_payAmount);
        tPayPointAmount = (TextView) findViewById(R.id.id_pointAmount);
        tPayCouponAmount = (TextView) findViewById(R.id.id_coupon_amount);

        tOrderAmount.setText(StringUtils.formatIntMoney(amount));
        tPayAmount.setText(StringUtils.formatIntMoney(amount));


        btnPreauth = (LinearLayout) findViewById(R.id.pay_preauth);
        btnAuthCancel = (LinearLayout) findViewById(R.id.pay_authCancel);
        btnAuthComplete = (LinearLayout) findViewById(R.id.pay_authComplete);
        btnVoidAuthComplete = (LinearLayout) findViewById(R.id.pay_voidAuthComplete);


        btnPrint = (Button) findViewById(R.id.id_print);
        btnPrintfinish = (Button) findViewById(R.id.id_finish);
        btnNopayAmount = (Button) findViewById(R.id.id_no_pay_amount);
        btnQuery = (Button) findViewById(R.id.id_query);
        btnQueryEnd = (Button) findViewById(R.id.id_terminal_query_sure);


        ll_payType = (LinearLayout) findViewById(R.id.ll_pay_type);
        ll_payFinish = (LinearLayout) findViewById(R.id.ll_pay_finish);
        ll_payQuery = (LinearLayout) findViewById(R.id.ll_pay_query);

    }





    private void addListenster() {

        btnPreauth.setOnClickListener(this);
        btnAuthCancel.setOnClickListener(this);
        btnAuthComplete.setOnClickListener(this);
        btnVoidAuthComplete.setOnClickListener(this);


        btnPrint.setOnClickListener(this);
        btnPrintfinish.setOnClickListener(this);
        btnNopayAmount.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
        btnQueryEnd.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CommonFunc.startAction(this, InputAmountActivity2.class, true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_print:

                Gson gson = new Gson();
                TransUploadRequest data = gson.fromJson(printerData.getTransUploadData(), TransUploadRequest.class);
                LogUtils.e(data.toString());
                getPrinterData(data);

                break;
            case R.id.id_finish:
            case R.id.id_terminal_query_sure: {
                CommonFunc.startAction(this, InputAmountActivity2.class, true);
            }
                break;
            case R.id.pay_preauth:
                preauth();
                break;
            case R.id.pay_authCancel:
                authCancel();
                break;
            case R.id.pay_authComplete:
                authComplete();
                break;
            case R.id.pay_voidAuthComplete:
                voidAuthComplete();
                break;
            default:
                break;
        }
    }


    /**
     * 预授权
     */
    private void preauth() {

        currentPayType = Constants.PAY_WAY_PREAUTH;
        CommonFunc.pay(this, 0, "300000", StringUtils.formatIntMoney(amount), "");
    }


    /**
     * 预授权撤销
     */
    private void authCancel(){
        currentPayType = Constants.PAY_WAY_AUTHCANCEL;
        CommonFunc.pay(ZfPayPreauthActivity.this, 0, "400000", "", "");
    }


    /**
     * 预授权完成
     */
    private void authComplete(){
        currentPayType = Constants.PAY_WAY_AUTHCOMPLETE;
        CommonFunc.pay(ZfPayPreauthActivity.this, 0, "330000", "", "");
    }

    private void voidAuthComplete(){
        currentPayType = Constants.PAY_WAY_VOID_AUTHCOMPLETE;
        CommonFunc.pay(ZfPayPreauthActivity.this, 0, "440000", "", "");
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
        printerData.setPayType(currentPayType);

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
                    String sysoldtraceno = data.getStringExtra("sysoldtraceno");
                    String detail = data.getExtras().getString("txndetail");
                    if ("0".equals(payType)){ //银行卡
                        if (currentPayType == Constants.PAY_WAY_PREAUTH || currentPayType == Constants.PAY_WAY_AUTHCOMPLETE) {
                            setFlot(detail);
                            //设置流水上送需要上送的参数
                            TransUploadRequest request = CommonFunc.setTransUploadData(mContext,printerData,
                                    CommonFunc.recoveryMemberInfo(ZfPayPreauthActivity.this),
                                    CommonFunc.getNewClientSn(mContext),
                                    printerData.getVoucherNo(), printerData.getReferNo());

                            //打印的订单号与流水上送的统一
                            printerData.setClientOrderNo(request.getClientOrderNo());

                            //流水上送
                            transUploadAction1(request);
                        }else if (currentPayType == Constants.PAY_WAY_AUTHCANCEL){
                            setFlot(detail);
                            if (getAuthCode(printerData.getAuthNo(), Constants.PAY_WAY_PREAUTH)) {
                                setTransCancel(currentPayType);
                            }else{
                                ToastUtils.CustomShow(mContext,"本地没查到该交易授权码");
                            }
                        }else if (currentPayType == Constants.PAY_WAY_VOID_AUTHCOMPLETE){
                            setFlot(detail);
                            if (getTraceNo(sysoldtraceno, Constants.PAY_WAY_AUTHCOMPLETE)) {
                                setTransCancel(currentPayType);
                            }else{
                                ToastUtils.CustomShow(mContext,"本地没查到该交易凭证号");
                            }
                        }

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
        }
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
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
//                dialog.dismiss();
//                AppManager.getAppManager().finishAllActivity();
//                if (Config.OPERATOR_UI_BEFORE) {
//                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity.class, false);
//                } else {
//                    CommonFunc.startAction(ZfPayPreauthActivity.this, OperatorLoginActivity1.class, false);
//                }
            }
        });
    }



    /**
     * 撤销流水上送
     */
    private void setTransCancel(int type) {
        final TransUploadRequest request = new TransUploadRequest();
        String orderId = CommonFunc.getNewClientSn(mContext);
        printerData.setClientOrderNo(orderId);
        printerData.setOldOrderId(orderNo);
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setAction("2");
        request.setOld_trade_order_num(orderNo);
        request.setNew_trade_order_num(orderId);
        request.setPayType(type);
        request.setAuthCode(printerData.getVoucherNo());
//        request.setClientOrderNo(orderNo);
        request.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));

        this.sbsAction.transCancelRefund(this, request, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                showLayout();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "上传成功");
                printerData.setRefundUpload(true);
                //这个地方用来 在交易记录里去打印用的
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                // 备份下交易流水数据
                setTransUpLoadData(request);
                printerData.setApp_type(app_type);



                PrinterDataSave();
                // 打印
//                Printer.print(printerData, ZfPayPreauthActivity.this);
                ToolNewLand.getToolNewLand().printText(printerData);


            }

            @Override
            public void onFailure(String errorEvent, String message) {
                showLayout();
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, "上传失败");
                // 备份下交易流水数据
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                setTransUpLoadData(request);
                // 保存打印的数据，不保存图片数据
                printerData.setApp_type(app_type);
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, ZfPayPreauthActivity.this);
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
                ToastUtils.CustomShow(ZfPayPreauthActivity.this, errorEvent + "#" + message);
                showLayout();

                setTransUpLoadData(request);
                // 设置当前交易流水需要上送
                printerData.setUploadFlag(true);
                printerData.setApp_type(app_type);


                // 保存打印的数据，不保存图片数据
                PrinterDataSave();

                // 打印
//                Printer.print(printerData, ZfPayPreauthActivity.this);
                ToolNewLand.getToolNewLand().printText(printerData);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
//                dialog.dismiss();
//                AppManager.getAppManager().finishAllActivity();



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
        ALog.json(data);
        printerData.setTransUploadData(data);
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
//            Printer.getInstance(ZfPayPreauthActivity.this).print(printerData, ZfPayPreauthActivity.this);
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

    private void setCounponData(List<Couponsn> data) {
        Gson gson = new GsonBuilder().serializeNulls().create();
        String counponStr = gson.toJson(data);
        printerData.setCouponData(counponStr);
    }


    private boolean getTraceNo(String traceNo, int type) {

        if (StringUtils.isBlank(traceNo)){
            return false;
        }
        // 从交易记录中读取数据
        allData = DataSupport.order("id desc").limit(100).find(SbsPrinterData.class);
        if (allData.size() <= 0) {
            ToastUtils.CustomShow(this, "没有交易记录");
            return false;
        }

        // 遍历
        for (int i = 0; i < allData.size(); i++) {
            // 遍历刷卡支付
            if (allData.get(i).getPayType() == type && !StringUtils.isEmpty(allData.get(i).getVoucherNo())) {
                if (StringUtils.isEquals(allData.get(i).getVoucherNo(),traceNo)) {
                    //获取交易的订单号
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(allData.get(i).getTransUploadData(), TransUploadRequest.class);

                    if (data != null) {
                        orderNo = data.getClientOrderNo();
                    }
                    return true;
                }
            }
        }
        ToastUtils.CustomShow(this, "没有该笔交易");
        return false;
    }


    private boolean getAuthCode(String authCode, int type) {

        // 从交易记录中读取数据
        allData = DataSupport.order("id desc").limit(100).find(SbsPrinterData.class);
        if (allData.size() <= 0) {
            ToastUtils.CustomShow(this, "没有交易记录");
            return false;
        }

        // 遍历
        for (int i = 0; i < allData.size(); i++) {
            // 遍历刷卡支付
            if (allData.get(i).getPayType() == type && !StringUtils.isEmpty(allData.get(i).getAuthNo())) {
                if (StringUtils.isEquals(allData.get(i).getAuthNo(),authCode)) {
                    //获取交易的订单号
                    Gson gson = new Gson();
                    TransUploadRequest data = gson.fromJson(allData.get(i).getTransUploadData(), TransUploadRequest.class);

                    if (data != null) {
                        orderNo = data.getClientOrderNo();
                    }
                    return true;
                }
            }
        }
        ToastUtils.CustomShow(this, "没有该笔交易");
        return false;
    }



}

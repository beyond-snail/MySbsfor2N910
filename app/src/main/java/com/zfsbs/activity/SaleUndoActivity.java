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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.CommonDialog;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.EncryptMD5Util;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SbsPrinterData;
import com.zfsbs.model.SmResponse;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.myapplication.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.zfsbs.config.Constants.REQUEST_CAPTURE_QB;
import static com.zfsbs.config.Constants.REQUEST_CASH;

public class SaleUndoActivity extends BaseActivity implements OnClickListener {

    private LinearLayout llPassword;
    private LinearLayout llTraceNo;
    private LinearLayout llText;
    private EditText etPassword;
    private EditText etTraceNo;
    private Button btnPassword;
    private Button btnTraceNo;
    private Button btnInfo;
    private Button btnClose;
    private TextView tv;

    private List<SbsPrinterData> allData;
    private int index;
    private String orderNo;

    private int app_type = 0;
    private String phone;
    private int points = 0;
    private boolean isMember;
    private String oldOrderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        setContentView(R.layout.activity_sale_undo);
//        AppManager.getAppManager().addActivity(this);
        initTitle("消费撤销");
        initView();
        addListener();
    }

    private void initView() {
        etPassword = (EditText) findViewById(R.id.id_input_password);
        etTraceNo = (EditText) findViewById(R.id.id_input_trace_no);
        btnPassword = (Button) findViewById(R.id.id_password_sure);
        btnTraceNo = (Button) findViewById(R.id.id_traceNO_sure);
        btnInfo = (Button) findViewById(R.id.id_info_sure);
        btnClose = (Button) findViewById(R.id.id_close);

        llPassword = (LinearLayout) findViewById(R.id.id_ll_MasterId);
        llTraceNo = (LinearLayout) findViewById(R.id.id_ll_trace_no);
        llText = (LinearLayout) findViewById(R.id.id_ll_text);

        tv = (TextView) findViewById(R.id.id_tv);


//        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);
    }

    private void addListener() {
        btnPassword.setOnClickListener(this);
        btnTraceNo.setOnClickListener(this);
        btnInfo.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_password_sure:
                isCheckPassword();
                break;
            case R.id.id_traceNO_sure:
                isCheckTraceNo();
                break;
            case R.id.id_info_sure:
                SaleUndoAction();
                break;
            case R.id.id_close:
                finish();
                break;
            default:
                break;
        }
    }

    private void SaleUndoAction() {
//        int trace_no = Integer.parseInt(etTraceNo.getText().toString());
        String traceNo = StringUtils.removeBlank(etTraceNo.getText().toString(), ' ');
        LogUtils.e(traceNo + "");

        CommonFunc.undo(this, 0, "200000", "", "", traceNo);

//        String mid = MyApplication.getInstance().getLoginData().getMerchantNo();
//        String tid = MyApplication.getInstance().getLoginData().getTerminalNo();
//        PayCommon.voidSale(this, trace_no, mid, tid, new PayCommon.ComTransResult<ComTransInfo>() {
//            @Override
//            public void success(ComTransInfo transInfo) {
//                LogUtils.e("SaleUndoAction", transInfo.toString());
//                setUndoPrintData(transInfo);
//
//
//                    setTransCancel();
//
//
//            }
//
//            @Override
//            public void failed(String error) {
//                final CommonDialog confirmDialog = new CommonDialog(SaleUndoActivity.this, error);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {

            switch (resultCode) {
                case Activity.RESULT_OK:

                    String payType = data.getStringExtra("proc_tp");
                    String detail = data.getExtras().getString("txndetail");
                    if ("0".equals(payType)){ //银行卡
                        setUndo(detail);
                        setTransCancel();
                    }

//                    mTvResult.setText("msg_tp:" + data.getStringExtra("msg_tp") + "\n" +
//                            "pay_tp:" + data.getStringExtra("pay_tp") + "\n" +
//                            "proc_tp:" + data.getStringExtra("proc_tp") + "\n" +
//                            "proc_cd:" + data.getStringExtra("proc_cd") + "\n" +
//                            "amt:" + data.getStringExtra("amt") + "\n" +
//                            "order_no:" + data.getStringExtra("order_no") + "\n" +
//                            "appid:" + data.getStringExtra("appid") + "\n" +
//                            "time_stamp:" + data.getStringExtra("time_stamp") + "\n" +
//                            "print_info:" + data.getStringExtra("print_info") + "\n" +
//                            "batchbillno:" + data.getStringExtra("batchbillno") + "\n" +
//                            "merid:" + data.getStringExtra("merid") + "\n" +
//                            "termid:" + data.getStringExtra("termid") + "\n");
//                    mTvResult.append("txndetail:" + data.getExtras().getString("txndetail"));
                    break;
                case Activity.RESULT_CANCELED:
//                    mTvResult.setText("reason:" + data.getStringExtra("reason"));
                    break;
                case -2:
//                    mTvResult.setText("reason" + data.getStringExtra("reason"));
                    break;
                default:
                    break;
            }
        }
    }

    private void setUndo(String data){

        Gson gson = new Gson();
        SmResponse smResponse = gson.fromJson(data, SmResponse.class);


        printerData.setMerchantName(smResponse.getMername());
        printerData.setMerchantNo(smResponse.getMerid());
        printerData.setTerminalId(smResponse.getTermid());
        printerData.setOperatorNo((String) SPUtils.get(this, Constants.USER_NAME, ""));
        printerData.setAcquirer(smResponse.getAcqno());
        printerData.setIssuer(smResponse.getIison());
        printerData.setCardNo(smResponse.getPriaccount());
        printerData.setTransType(2 + "");
        printerData.setExpDate(smResponse.getExpdate());
        printerData.setBatchNO(smResponse.getBatchno());
        printerData.setVoucherNo(smResponse.getSystraceno());
        printerData.setDateTime(
                StringUtils.formatTime(smResponse.getTranslocaldate()+smResponse.getTranslocaltime()));
        printerData.setAuthNo(smResponse.getAuthcode());
        LogUtils.e(smResponse.getRefernumber());
        printerData.setReferNo(smResponse.getRefernumber());
        printerData.setPointCoverMoney(0);
        printerData.setCouponCoverMoney(0);
        printerData.setOrderAmount(Integer.parseInt(StringUtils.changeY2F(smResponse.getTransamount())));
        printerData.setAmount(smResponse.getTransamount());
        printerData.setPayType(Constants.PAY_WAY_UNDO);
    }

//    protected void setUndoPrintData(ComTransInfo transInfo) {
//        printerData.setMerchantName(MyApplication.getInstance().getLoginData().getTerminalName());
//        printerData.setMerchantNo(transInfo.getMid());
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
//        LogUtils.e(transInfo.getRrn());
//        printerData.setReferNo(transInfo.getRrn());
//        printerData.setPointCoverMoney(0);
//        printerData.setCouponCoverMoney(0);
//        // transInfo.setTransAmount(56000);
//        printerData.setOrderAmount(transInfo.getTransAmount());
//        printerData.setAmount(StringUtils.formatIntMoney(transInfo.getTransAmount()));
//        printerData.setPayType(Constants.PAY_WAY_UNDO);
//
////        amount = transInfo.getTransAmount();
//    }


    private void setTransUpLoadData(TransUploadRequest request) {
        Gson gson = new Gson();
        String data = gson.toJson(request);
        LogUtils.e("SaleUndoActivity", data);
        printerData.setTransUploadData(data);
    }


    /**
     * 撤销流水上送
     */
    private void setTransCancel() {
        final TransUploadRequest request = new TransUploadRequest();
        String orderId = CommonFunc.getNewClientSn(mContext);
        printerData.setClientOrderNo(orderId);
        printerData.setOldOrderId(orderNo);
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setAction("2");
        request.setOld_trade_order_num(orderNo);
        request.setNew_trade_order_num(orderId);
        request.setPayType(Constants.PAY_WAY_UNDO);
        request.setAuthCode(printerData.getVoucherNo());
//        request.setClientOrderNo(orderNo);
        request.setT(StringUtils.getdate2TimeStamp(printerData.getDateTime()));

        this.sbsAction.transCancelRefund(this, request, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(SaleUndoActivity.this, "撤销成功");
                printerData.setRefundUpload(true);
                //这个地方用来 在交易记录里去打印用的
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                // 备份下交易流水数据
                setTransUpLoadData(request);
                printerData.setApp_type(app_type);



                    PrinterDataSave();
                    // 打印
//                    Printer.print(printerData, SaleUndoActivity.this);
                    finish();

            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(SaleUndoActivity.this, "撤销成功");
                // 备份下交易流水数据
                request.setSid(MyApplication.getInstance().getLoginData().getSid());
                setTransUpLoadData(request);
                // 保存打印的数据，不保存图片数据
                printerData.setApp_type(app_type);
                PrinterDataSave();
                // 打印
//                Printer.print(printerData, SaleUndoActivity.this);
                finish();
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {
                AppManager.getAppManager().finishAllActivity();
                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(SaleUndoActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(SaleUndoActivity.this, OperatorLoginActivity1.class, false);
                }
            }
        });
    }







    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap point_bitmap = bundle.getParcelable("point_bitmap");
            Bitmap title_bitmap = bundle.getParcelable("title_bitmap");
            printerData.setPoint_bitmap(point_bitmap);
            printerData.setCoupon_bitmap(title_bitmap);


            // 打印
//            Printer.print(printerData, SaleUndoActivity.this);
            finish();
        }

        ;
    };



    private void PrinterDataSave() {

        CommonFunc.PrinterDataDelete();
        printerData.setStatus(true);
        if (printerData.save()) {
            LogUtils.e("打印数据存储成功");
        } else {
            LogUtils.e("打印数据存储失败");
        }
    }

    private void isCheckTraceNo() {
        if (StringUtils.isEmpty(etTraceNo.getText().toString())) {
            ToastUtils.CustomShow(this, "请输入原交易凭证号");
            return;
        }
        saleUndoInfo();
    }

    private void saleUndoInfo() {

        // 从交易记录中读取数据
        allData = DataSupport.order("id desc").limit(100).find(SbsPrinterData.class);
        if (allData.size() <= 0) {
            ToastUtils.CustomShow(this, "没有交易记录");
            return;
        }


        // 遍历
        for (int i = 0; i < allData.size(); i++) {
            // 遍历刷卡支付
            if (allData.get(i).getPayType() == Constants.PAY_WAY_FLOT && !StringUtils.isEmpty(allData.get(i).getVoucherNo())) {
                if (StringUtils.isEquals(allData.get(i).getVoucherNo(), etTraceNo.getText().toString())) {
                    showTransInfo(allData.get(i));
                    index = i;
                    return;
                }
            }
        }
        ToastUtils.CustomShow(this, "没有该笔交易");

    }

    private void showTransInfo(SbsPrinterData sbsPrinterData) {
        llTraceNo.setVisibility(View.GONE);
        llText.setVisibility(View.VISIBLE);
        String str = "卡号：\r\n" + sbsPrinterData.getCardNo() + "\r\n" + "金额：" + sbsPrinterData.getAmount() + "\r\n"
                + "交易时间：\r\n" + sbsPrinterData.getDateTime();
        tv.setText(str);

        //获取交易的订单号
        Gson gson = new Gson();
        TransUploadRequest data = gson.fromJson(sbsPrinterData.getTransUploadData(), TransUploadRequest.class);

        if (data != null) {
            orderNo = data.getClientOrderNo();
        }

        oldOrderId = sbsPrinterData.getClientOrderNo();
        app_type=sbsPrinterData.getApp_type();
        phone=sbsPrinterData.getPhoneNo();
        points = sbsPrinterData.getPoint();
        isMember = sbsPrinterData.isMember();
    }

    private void isCheckPassword() {
        String pass = (String) SPUtils.get(this, Constants.MASTER_PASS, Constants.DEFAULT_MASTER_PASS);
        if (StringUtils.isEmpty(etPassword.getText().toString())) {
            ToastUtils.CustomShow(this, "请输入主管理密码");
            return;
        }
        if (!StringUtils.isEquals(pass, etPassword.getText().toString())) {
            ToastUtils.CustomShow(this, "主管理密码错误");
            return;
        }
        llPassword.setVisibility(View.GONE);
        llTraceNo.setVisibility(View.VISIBLE);
        etTraceNo.setFocusable(true);
        etTraceNo.requestFocus();
    }
}

package com.zfsbs.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.MemberNoDialog;
//import com.tool.utils.msrcard.MsrCard;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.RicherQb;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.CouponsResponse;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.myapplication.MyApplication;
import com.zfsbs.tool.CustomDialog_2;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static com.zfsbs.common.CommonFunc.getNewClientSn;
import static com.zfsbs.common.CommonFunc.startAction;
import static com.zfsbs.common.CommonFunc.startResultAction;


public class InputAmountActivity extends BaseActivity implements OnClickListener {

    public static final int REQUEST_CAPTURE = 0;

    public static final int REQUEST_YY = 1;

    private TextView tKey1;
    private TextView tKey2;
    private TextView tKey3;
    private TextView tKey4;
    private TextView tKey5;
    private TextView tKey6;
    private TextView tKey7;
    private TextView tKey8;
    private TextView tKey9;
    private TextView tKey0;
    private TextView tkey00;
    private TextView tkeyPoint;

    private TextView tKeyblack;
    private TextView tKeyCaculate;
    private TextView tAmount;

    private EditText tvScanNo;

    private int amount = 0;

    private String txt_show;

    private int yyAmount = 0;
    private Long yyId;
    private String couponCode;

    private int app_type = 0;

    private String g_phone; //输入的手机号

    private boolean yy_flag = false; //是否使用异业优惠券
    private int limitAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_input_amount);
//        AppManager.getAppManager().addActivity(this);
        initTitle("输入金额");
        initView();
        addLinstener();
        initData();

    }

    private void initData() {
        //是否是赢消费
        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);


        ToolNewLand.getToolNewLand().searchCard(new ToolNewLand.DeviceListener() {
            @Override
            public void success(String data) {
                final String cardNo = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvScanNo.setText(cardNo);
                        tvScanNo.setSelection(cardNo.length());
                    }
                });

            }

            @Override
            public void fail(String data) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolNewLand.getToolNewLand().stopSearch();
    }

    private void initView() {

        Button btnYy = (Button) findViewById(R.id.id_edit);
        btnYy.setVisibility(View.VISIBLE);
        btnYy.setOnClickListener(this);
        btnYy.setText("增加优惠");

        tKey0 = (TextView) findViewById(R.id.id_key_0);
        tkey00 = (TextView) findViewById(R.id.id_key_00);
        tKey1 = (TextView) findViewById(R.id.id_key_1);
        tKey2 = (TextView) findViewById(R.id.id_key_2);
        tKey3 = (TextView) findViewById(R.id.id_key_3);
        tKey4 = (TextView) findViewById(R.id.id_key_4);
        tKey5 = (TextView) findViewById(R.id.id_key_5);
        tKey6 = (TextView) findViewById(R.id.id_key_6);
        tKey7 = (TextView) findViewById(R.id.id_key_7);
        tKey8 = (TextView) findViewById(R.id.id_key_8);
        tKey9 = (TextView) findViewById(R.id.id_key_9);
        tkeyPoint = (TextView) findViewById(R.id.id_key_point);

        tKeyblack = (TextView) findViewById(R.id.id_key_back);
        tKeyCaculate = (TextView) findViewById(R.id.id_key_caculate);
        tAmount = (TextView) findViewById(R.id.id_tv_amount);

        tAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isnum = (!StringUtils.isBlank(txt_show))&&isNumber(txt_show)&&(!txt_show.equals(0));
                setTvEnable(R.id.id_key_caculate, isnum);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });



        imageView(R.id.btn_scan_no).setOnClickListener(this);


        tvScanNo = editText(R.id.tv_scan_no);
    }

    private void addLinstener() {
        tKey0.setOnClickListener(this);
        tkey00.setOnClickListener(this);
        tKey1.setOnClickListener(this);
        tKey2.setOnClickListener(this);
        tKey3.setOnClickListener(this);
        tKey4.setOnClickListener(this);
        tKey5.setOnClickListener(this);
        tKey6.setOnClickListener(this);
        tKey7.setOnClickListener(this);
        tKey8.setOnClickListener(this);
        tKey9.setOnClickListener(this);
        tKeyblack.setOnClickListener(this);
        tKeyCaculate.setOnClickListener(this);
        tkeyPoint.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_key_0:
//                setTextAmount(0);
                key_event("0");
                break;
            case R.id.id_key_00:
//                setTextDouble();
                key_event("00");
                break;
            case R.id.id_key_1:
//                setTextAmount(1);
                key_event("1");
                break;
            case R.id.id_key_2:
//                setTextAmount(2);
                key_event("2");
                break;
            case R.id.id_key_3:
//                setTextAmount(3);
                key_event("3");
                break;
            case R.id.id_key_4:
//                setTextAmount(4);
                key_event("4");
                break;
            case R.id.id_key_5:
//                setTextAmount(5);
                key_event("5");
                break;
            case R.id.id_key_6:
//                setTextAmount(6);
                key_event("6");
                break;
            case R.id.id_key_7:
//                setTextAmount(7);
                key_event("7");
                break;
            case R.id.id_key_8:
//                setTextAmount(8);
                key_event("8");
                break;
            case R.id.id_key_9:
//                setTextAmount(9);
                key_event("9");
                break;
            case R.id.id_key_back:
//                amount = amount / 10;
//                tAmount.setText(StringUtils.formatAmount(amount));
                del_event();
                break;
            case R.id.id_key_point:
                key_event(".");
                break;
            case R.id.id_key_caculate:
                Caculate();
                break;
            case R.id.id_edit:
                CommonFunc.startResultAction(InputAmountActivity.this, YyVerificationActivity.class, null, REQUEST_YY);
                break;
            case R.id.btn_scan_no:
//                CommonFunc.startResultAction(InputAmountActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE);
                ToolNewLand.getToolNewLand().scan(new ToolNewLand.DeviceListener() {
                    @Override
                    public void success(final String data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 处理扫描结果（在界面上显示）
                                tvScanNo.setText(data);
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
            case R.id.tv_del:
                linearLayout(R.id.ll_show_yy).setVisibility(View.INVISIBLE);
                textView(R.id.id_show_yy).setText("");
                yy_flag = false;
                break;
            default:
                break;
        }
    }



    private void Caculate() {

        amount = (int) getPayAmount();

        //判断签到
        if (CommonFunc.isLogin(this, Constants.SBS_LOGIN_TIME, Constants.DEFAULT_SBS_LOGIN_TIME)){
            AppManager.getAppManager().finishAllActivity();
            if (Config.OPERATOR_UI_BEFORE) {
                CommonFunc.startAction(InputAmountActivity.this, OperatorLoginActivity.class, false);
            } else {
                CommonFunc.startAction(InputAmountActivity.this, OperatorLoginActivity1.class, false);
            }

            return;
        }

        //是否使用异业优惠券
        if (yy_flag){
            if (limitAmount > amount){
                ToastUtils.showShort(mContext, "该券满"+StringUtils.formatIntMoney(limitAmount)+"元使用");
                return;
            }
            memberTransAmountAction();
            return;
        }



        if (amount > 0 && amount <= 999999999) {

            if (StringUtils.isBlank(tvScanNo.getText().toString().trim())) {

                //备份订单号
                SetClientOrder order = new SetClientOrder();
                order.setStatus(false);
                CommonFunc.setMemberClientOrderNo(InputAmountActivity.this, order);

                MemberTransAmountResponse member = new MemberTransAmountResponse();
                member.setRealMoney(amount);
                member.setTradeMoney(amount);
                CommonFunc.setBackMemberInfo(InputAmountActivity.this, member);
                startAction(InputAmountActivity.this, ZfPayActivity.class, true);
            }else{
                memberInfoAction(tvScanNo.getText().toString().trim());
            }

        } else {
            if (amount > 999999999) {
                ToastUtils.CustomShow(this, "金额过大");
            } else {
                ToastUtils.CustomShow(this, "请输入支付金额");
            }
        }
    }


    private void setTextAmount(int digital) {
        if (amount < 100000000) {
            amount = amount * 10 + digital;
            tAmount.setText(StringUtils.formatAmount(amount));
        }
    }

    private void setTextDouble() {
        if (amount < 100000000) {
            amount = amount * 10;
            if (amount < 100000000) {
                amount = amount * 10;
                tAmount.setText(StringUtils.formatAmount(amount));
            } else {
                amount = amount / 10;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAPTURE:
                // 处理扫描结果（在界面上显示）
//                String phoneNo = data.getStringExtra(CaptureActivity.SCAN_RESULT);
//                MemberNoDialog.setMemberNo(phoneNo);
                break;
            case REQUEST_YY:
                String name = data.getStringExtra("name");
                yyAmount = Integer.valueOf(data.getStringExtra("amount")).intValue();
                yyId = data.getLongExtra("yyId", 0);
                limitAmount = data.getIntExtra("limitAmount", 0);
                couponCode = data.getStringExtra("couponCode");
                linearLayout(R.id.ll_show_yy).setVisibility(View.VISIBLE);
                textView(R.id.id_show_yy).setVisibility(View.VISIBLE);
                textView(R.id.id_show_yy).setText(name+":"+StringUtils.formatIntMoney(yyAmount)+"元");

                yy_flag = true;
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//    private void isInputMemberNo() {
//        MemberNoDialog dialog = new MemberNoDialog(this, R.layout.activity_member_no2, new MemberNoDialog.OnClickInterface() {
//
//            @Override
//            public void onResultScanContent() {
//                startResultAction(InputAmountActivity.this, CaptureActivity.class, null, REQUEST_CAPTURE);
//            }
//
//            @Override
//            public void onClickRight() {
//
//                    MemberNoDialog.setMemberNo("");
//
//
//                    //备份订单号
//                    SetClientOrder order = new SetClientOrder();
//                    order.setStatus(false);
//                    CommonFunc.setMemberClientOrderNo(InputAmountActivity.this, order);
//
//                    MemberTransAmountResponse member = new MemberTransAmountResponse();
//                    member.setRealMoney(amount);
//                    member.setTradeMoney(amount);
//                    CommonFunc.setBackMemberInfo(InputAmountActivity.this, member);
//                    startAction(InputAmountActivity.this, ZfPayActivity.class, true);
//
//
//            }
//
//            @Override
//            public void onClickLeft(String result) {
//                if (app_type == Config.APP_SBS) {
//                    memberInfoAction(result);
//                }
//
//            }
//        });
//
////        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
////            @Override
////            public void onDismiss(DialogInterface dialogInterface) {
//////                MsrCard.getMsrCard(InputAmountActivity.this).closeMsrCard();
////            }
////        });
//        dialog.setCancelable(true);
//        dialog.show();
//
//    }






    /**
     * 商博士-会员信息
     * @param phone
     */
    private void memberInfoAction(String phone) {
        Long sid = MyApplication.getInstance().getLoginData().getSid();

        this.sbsAction.getMemberInfo(this, sid, phone, amount, "",new ActionCallbackListener<CouponsResponse>() {
            @Override
            public void onSuccess(CouponsResponse data) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("member", data);
                bundle.putString("amount", tAmount.getText().toString());
                startAction(InputAmountActivity.this, MemberActivity.class, bundle, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(InputAmountActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }



    private void memberTransAmountAction() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        final String orderNo = getNewClientSn(mContext);

        this.sbsAction.otherCouponLock(InputAmountActivity.this, sid, couponCode, orderNo, amount, new ActionCallbackListener<MemberTransAmountResponse>() {
            @Override
            public void onSuccess(MemberTransAmountResponse data) {


                //备份订单号
                SetClientOrder order = new SetClientOrder();
                order.setStatus(true);
                order.setClientNo(orderNo);
                CommonFunc.setMemberClientOrderNo(InputAmountActivity.this, order);


                data.setPoint(0);
                data.setPass("");
                data.setStkCardNo("");
                data.setCouponSns(couponCode);
                CommonFunc.setBackMemberInfo(InputAmountActivity.this, data);

                CommonFunc.startAction(InputAmountActivity.this, ZfPayActivity.class, true);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(InputAmountActivity.this, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }


    private double getPayAmount(){
        if(StringUtils.isBlank(txt_show))return 0;
        else {
            try{
//				double f_pay_amount = Double.parseDouble(txt_show);
//				double f_pay_amount_cent = f_pay_amount * 100;

                BigDecimal big = new BigDecimal(txt_show);
                double  f_pay_amount_cent = big.multiply(new BigDecimal(100)).doubleValue();
                return f_pay_amount_cent;
            }
            catch(Exception e){return 0;}
        }
    }

    private void key_event(String key){

        if(StringUtils.isBlank(txt_show)){
            if(key.equals("."))return;
            if(key.equals("00"))return;
        }
        else{
            if(txt_show.contains(".")&&key.equals("."))return;
            else if(txt_show.equals("0")&&key.equals("0"))return;
            else  if(Pattern.compile(".*\\.\\d{2}").matcher(txt_show).matches()){return;}
            else {
                String txt_show_tmp = txt_show.replaceFirst("\\..*", "");
                if(!StringUtils.isBlank(txt_show_tmp)&&txt_show_tmp.length()>8){return;}
            }
        }

        txt_show = (StringUtils.isBlank(txt_show)?"":txt_show) + key;
//        setTvText(R.id.tv_show, "￥"+txt_show);
        tAmount.setText(txt_show);
    }


    private boolean isNumber(String in){
        if(StringUtils.isBlank(in))return false;
        boolean isZero = Pattern.compile("(0|\\.)+").matcher(in).matches();
        if(isZero)return false;
        try{
            Float.parseFloat(in);
            return true;
        }
        catch(Exception e){return false;}
    }

    private void del_event(){
        if(StringUtils.isBlank(txt_show)){return;}
        else{
            int length = txt_show.length();
            txt_show = txt_show.substring(0, length-1);
            tAmount.setText(StringUtils.isBlank(txt_show)?"":(txt_show));
        }
    }



}

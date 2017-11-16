package com.zfsbs.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.model.MemberTransAmountResponse;


public class InputAmountActivity2 extends BaseActivity implements OnClickListener {

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

    private TextView tKeyblack;
    private TextView tKeyCaculate;
    private TextView tAmount;



    private int amount = 0;

    private int app_type = 0;

    private String g_phone; //输入的手机号

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
        app_type = (int) SPUtils.get(this, Config.APP_TYPE, Config.DEFAULT_APP_TYPE);//getIntent().getBooleanExtra("yxf", false);

    }



    private void initView() {

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

        tKeyblack = (TextView) findViewById(R.id.id_key_back);
        tKeyCaculate = (TextView) findViewById(R.id.id_key_caculate);
        tAmount = (TextView) findViewById(R.id.id_tv_amount);
        linearLayout(R.id.id_show_phone).setVisibility(View.INVISIBLE);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_key_0:
                setTextAmount(0);
                break;
            case R.id.id_key_00:
                setTextDouble();
                break;
            case R.id.id_key_1:
                setTextAmount(1);
                break;
            case R.id.id_key_2:
                setTextAmount(2);
                break;
            case R.id.id_key_3:
                setTextAmount(3);
                break;
            case R.id.id_key_4:
                setTextAmount(4);
                break;
            case R.id.id_key_5:
                setTextAmount(5);
                break;
            case R.id.id_key_6:
                setTextAmount(6);
                break;
            case R.id.id_key_7:
                setTextAmount(7);
                break;
            case R.id.id_key_8:
                setTextAmount(8);
                break;
            case R.id.id_key_9:
                setTextAmount(9);
                break;
            case R.id.id_key_back:
                amount = amount / 10;
                tAmount.setText(StringUtils.formatAmount(amount));
                break;
            case R.id.id_key_caculate:
                Caculate();
                break;

            default:
                break;
        }
    }

    private void Caculate() {

        //判断签到
        if (CommonFunc.isLogin(this, Constants.SBS_LOGIN_TIME, Constants.DEFAULT_SBS_LOGIN_TIME)){
            AppManager.getAppManager().finishAllActivity();
            if (Config.OPERATOR_UI_BEFORE) {
                CommonFunc.startAction(InputAmountActivity2.this, OperatorLoginActivity.class, false);
            } else {
                CommonFunc.startAction(InputAmountActivity2.this, OperatorLoginActivity1.class, false);
            }

            return;
        }

        if (amount > 0 && amount <= 999999999) {
            Bundle bundle = new Bundle();
            bundle.putInt("amount", amount);




            MemberTransAmountResponse member = new MemberTransAmountResponse();
            member.setRealMoney(amount);
            member.setTradeMoney(amount);
            CommonFunc.setBackMemberInfo(InputAmountActivity2.this, member);


            CommonFunc.startAction(InputAmountActivity2.this, ZfPayPreauthActivity.class, bundle, true);
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

}

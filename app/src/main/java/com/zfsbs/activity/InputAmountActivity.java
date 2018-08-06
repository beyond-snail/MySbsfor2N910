package com.zfsbs.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.CustomDialog;
import com.tool.utils.dialog.MemberNoDialog;
//import com.tool.utils.msrcard.MsrCard;
import com.tool.utils.utils.Arith;
import com.tool.utils.utils.LogUtils;
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
import com.zfsbs.model.Coupons;
import com.zfsbs.model.CouponsResponse;
import com.zfsbs.model.MemberTransAmountRequest;
import com.zfsbs.model.MemberTransAmountResponse;
import com.zfsbs.model.RicherGetMember;
import com.zfsbs.model.SetClientOrder;
import com.zfsbs.myapplication.MyApplication;
import com.zfsbs.tool.CustomDialog_2;
import com.zfsbs.view.MyDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//                setQyInfo();
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
        final String clientNo =  getNewClientSn(mContext);
        this.sbsAction.getMemberInfo(this, sid, phone, amount, "",clientNo,new ActionCallbackListener<CouponsResponse>() {
            @Override
            public void onSuccess(CouponsResponse data) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("member", data);
//                bundle.putString("amount", tAmount.getText().toString());
//                startAction(InputAmountActivity.this, MemberActivity.class, bundle, true);
                  setQyInfo(data);
//                //备份订单号
//                SetClientOrder order = new SetClientOrder();
//                order.setStatus(true);
//                order.setClientNo(clientNo);
//                CommonFunc.setMemberClientOrderNo(InputAmountActivity.this, order);
//
//                CommonFunc.setBackMemberInfo(InputAmountActivity.this, data);
//
//                startAction(InputAmountActivity.this, ZfPayActivity.class, true);
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


    private int amountToPoint(int amt, int pointChangeRate){
        double amountBig = Arith.mul(amt, pointChangeRate);
        double amountToPoint = Arith.divide(amountBig, 100);
        return (int) amountToPoint;
    }

    private void setQyInfo(CouponsResponse data){


        //计算优惠券最大值 和 积分最大使用
        //1、计算优惠券最大金额
        int couponNum = data.getCouponNum();
        int couponMoneyMax = 0;
        String couponSn = "";
        List<Coupons> coupons = (List<Coupons>) data.getCoupons();
        List<Integer> inputMax = new ArrayList<>();
        if(couponNum > 0){
            for (int i = 0; i < couponNum; i++){
                inputMax.add((Integer) coupons.get(i).getMoney());
            }
            if (inputMax.size() > 0){
                couponMoneyMax = Collections.max(inputMax);
            }

            for (int j = 0; j <couponNum; j++){
                if (couponMoneyMax == ((Integer)coupons.get(j).getMoney())){
                    couponSn = (String) coupons.get(j).getSn();
                }
            }

        }

        LogUtils.e("couponMoneyMax:" + couponMoneyMax);
        LogUtils.e("couponSn:" + couponSn);

        //2、计算最大使用积分数
        int pointMin = 0;
        int pointChangeRate = (int) data.getPointChangeRate();
        int AmtPoint = amountToPoint(amount, pointChangeRate);
        LogUtils.e("amountToPoint:" + AmtPoint);
        List<Integer> inputMin = new ArrayList<>();
        inputMin.add((int) AmtPoint);
        inputMin.add((Integer) data.getPoint());
        inputMin.add((Integer)data.getPointUseMax());
        pointMin = Collections.min(inputMin);
        LogUtils.e("pointMin:" + pointMin);


        mData.clear();

        setShowData("全选");
        setShowData("积分: "+pointMin);
        setShowData("优惠券: "+ StringUtils.formatIntMoney(couponMoneyMax));


        CreateDialog(data, pointMin, couponMoneyMax, couponSn);// 点击创建Dialog
    }

    private void setShowData(String data){
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("text", data);
        mData.add(item);
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



    private void memberTransAmountAction2(final CouponsResponse couponsResponse, final int pointMin, String couponSn) {
        final MemberTransAmountRequest request = new MemberTransAmountRequest();
        request.setSid(MyApplication.getInstance().getLoginData().getSid());
        request.setMemberCardNo(couponsResponse.getMemberCardNo());
        request.setPassword("");
        request.setTradeMoney(amount);
        request.setPoint(pointMin);
        request.setCouponSn(couponSn);
        request.setMemberName(couponsResponse.getMemberName());
        request.setClientOrderNo(getNewClientSn(mContext));

        this.sbsAction.memberTransAmount(InputAmountActivity.this, request, new ActionCallbackListener<MemberTransAmountResponse>() {
            @Override
            public void onSuccess(MemberTransAmountResponse data) {


                //备份订单号
                SetClientOrder order = new SetClientOrder();
                order.setStatus(true);
                order.setClientNo(request.getClientOrderNo());
                CommonFunc.setMemberClientOrderNo(InputAmountActivity.this, order);


                data.setPoint(pointMin);
                data.setPass("");
                data.setStkCardNo(couponsResponse.getIcCardNo());
                CommonFunc.setBackMemberInfo(InputAmountActivity.this, data);

                startAction(InputAmountActivity.this, ZfPayActivity.class, true);
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
                AppManager.getAppManager().finishAllActivity();

                if (Config.OPERATOR_UI_BEFORE) {
                    CommonFunc.startAction(InputAmountActivity.this, OperatorLoginActivity.class, false);
                } else {
                    CommonFunc.startAction(InputAmountActivity.this, OperatorLoginActivity1.class, false);
                }
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


    View getlistview;
//    String[] mlistText = { "全选", "积分", "优惠券" };
    ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
    SimpleAdapter adapter;
    Boolean[] bl = { false, false, false };




    class ItemOnClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            CheckBox cBox = (CheckBox) view.findViewById(R.id.X_checkbox);
            if (cBox.isChecked()) {
                cBox.setChecked(false);
            } else {
                Log.i("TAG", "取消该选项");
                cBox.setChecked(true);
            }

            if (position == 0 && (cBox.isChecked())) {
                //如果是选中 全选  就把所有的都选上 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = true;
                }
                adapter.notifyDataSetChanged();
            } else if (position == 0 && (!cBox.isChecked())) {
                //如果是取消全选 就把所有的都取消 然后更新
                for (int i = 0; i < bl.length; i++) {
                    bl[i] = false;
                }
                adapter.notifyDataSetChanged();
            }
            if (position != 0 && (!cBox.isChecked())) {
                // 如果把其它的选项取消   把全选取消
                bl[0] = false;
                bl[position]=false;
                adapter.notifyDataSetChanged();
            } else if (position != 0 && (cBox.isChecked())) {
                //如果选择其它的选项，看是否全部选择
                //先把该选项选中 设置为true
                bl[position]=true;
                int a = 0;
                for (int i = 1; i < bl.length; i++) {
                    if (bl[i] == false) {
                        //如果有一个没选中  就不是全选 直接跳出循环
                        break;
                    } else {
                        //计算有多少个选中的
                        a++;
                        if (a == bl.length - 1) {
                            //如果选项都选中，就把全选 选中，然后更新
                            bl[0] = true;
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

    }

    public void CreateDialog(final CouponsResponse data, final int pointMin, final int couponMoneyMax, final String couponSn) {



        // 动态加载一个listview的布局文件进来
        LayoutInflater inflater = LayoutInflater.from(mContext);
        getlistview = inflater.inflate(R.layout.qy_dialog, null);

        // 给ListView绑定内容
        ListView listview = (ListView) getlistview.findViewById(R.id.X_listview);
        adapter = new SetSimpleAdapter(mContext, mData, R.layout.qy_listitem, new String[] { "text" },
                new int[] { R.id.X_item_text });
        // 给listview加入适配器
        listview.setAdapter(adapter);
        listview.setItemsCanFocus(false);
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listview.setOnItemClickListener(new ItemOnClick());


        MyDialog.Builder builder = new MyDialog.Builder(mContext);
        builder.setTitle("请选择权益");
        builder.setContentView(getlistview);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                int point = 0;
                String coupon = "";

                if (bl[0] == true && bl[1] == true && bl[2] == true){
                    //优先使用优惠券
                    if (amount <= couponMoneyMax){
                        point = 0;

                    }else{
                        //使用积分和优惠券混合
                        int balanceAmt = amount - couponMoneyMax;
                        int balancePoint = amountToPoint(balanceAmt, data.getPointChangeRate());
                        if (balancePoint < pointMin){
                            point = balancePoint;
                        }else{
                            point = pointMin;
                        }
                    }
                    coupon = couponSn;
                }else if (bl[1] == true && bl[2] == false){
                    point = pointMin;
                }else if (bl[1] == false && bl[2] == true){
                    point = 0;
                    coupon = couponSn;
                }




                memberTransAmountAction2(data, point, coupon);
            }
        });
//        builder.setNegativeButton("取消", new DialogOnClick());
        builder.create().show();

    }

    class DialogOnClick implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    //确定按钮的事件
                    dialog.dismiss();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    //取消按钮的事件
                    dialog.dismiss();
                    break;
                default:
                    break;
            }
        }
    }

    //重写simpleadapterd的getview方法
    class SetSimpleAdapter extends SimpleAdapter {

        public SetSimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
                                int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LinearLayout.inflate(getBaseContext(), R.layout.qy_listitem, null);
            }
            CheckBox ckBox = (CheckBox) convertView.findViewById(R.id.X_checkbox);
            //每次都根据 bl[]来更新checkbox
            if (bl[position] == true) {
                ckBox.setChecked(true);
            } else if (bl[position] == false) {
                ckBox.setChecked(false);
            }
            return super.getView(position, convertView, parent);
        }
    }


}

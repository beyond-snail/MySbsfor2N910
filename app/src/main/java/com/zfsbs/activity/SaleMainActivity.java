package com.zfsbs.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.model.SbsPrinterData;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.dialog.LoadingDialog;
import com.tool.utils.utils.AidlUtils;
import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.NetUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.tool.utils.view.MyGridView;
import com.zfsbs.R;
import com.zfsbs.adapter.MyMenuAdapter;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.config.Config;
import com.zfsbs.config.Constants;
import com.zfsbs.config.EnumConstsSbs;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ChargeBlance;
import com.zfsbs.model.Menu;
import com.zfsbs.model.RechargeUpLoad;
import com.zfsbs.model.TransUploadRequest;
import com.zfsbs.model.TransUploadResponse;

import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;


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
    protected void onResume() {
        super.onResume();
//        LogUtils.e("saleMainActivity initData");
        checkFailTrans();
    }


    private void checkFailTrans(){

        if (!NetUtils.isConnected(this)){
            return;
        }
        String where = "UploadFlag=1";
        List<SbsPrinterData> list =  DataSupport.where(where).find(SbsPrinterData.class);


        upload(list);


    }


    private void upload(final List<SbsPrinterData> recordInfos){

        if (recordInfos.size() <= 0){
            return;
        }



        final SbsPrinterData recordInfo = recordInfos.get(0);


        if (recordInfo.getPayType() == Constants.PAY_WAY_RECHARGE_ALY ||
                recordInfo.getPayType() == Constants.PAY_WAY_RECHARGE_WX ||
                recordInfo.getPayType() == Constants.PAY_WAY_RECHARGE_UNIPAY ||
                recordInfo.getPayType() == Constants.PAY_WAY_PAY_FLOT ||
                recordInfo.getPayType() == Constants.PAY_WAY_RECHARGE_CASH) {
            Gson gson = new Gson();
            final RechargeUpLoad request = gson.fromJson(recordInfo.getRechargeUpload(), RechargeUpLoad.class);
//            rechargeUpload(request, recordInfo);
            if (request == null){
                return;
            }

            sbsAction.rechargePay(mContext, request, new ActionCallbackListener<ChargeBlance>() {
                @Override
                public void onSuccess(ChargeBlance data) {
                    //更新
                    ContentValues values = new ContentValues();
                    values.put("promotion_num", request.getPromotion_num());
                    values.put("pacektRemian", data.getPacektRemian());
                    values.put("realize_card_num", data.getRealize_card_num());
                    values.put("member_name", data.getMember_name());
                    values.put("UploadFlag", false);
                    DataSupport.update(SbsPrinterData.class, values, recordInfo.getId());


                    recordInfos.remove(0);

                    upload(recordInfos);
                }

                @Override
                public void onFailure(String errorEvent, String message) {

                }

                @Override
                public void onFailurTimeOut(String s, String error_msg) {

                }

                @Override
                public void onLogin() {

                }
            });


        } else {
            Gson gson = new Gson();
            TransUploadRequest data = gson.fromJson(recordInfo.getTransUploadData(), TransUploadRequest.class);
//            transUploadAction(data, recordInfo);
            if (data == null) {
                return;
            }
            LogUtils.e(data.toString());


            this.sbsAction.transUpload(this, data, new ActionCallbackListener<TransUploadResponse>() {
                @Override
                public void onSuccess(TransUploadResponse data) {


                    Gson gson = new GsonBuilder().serializeNulls().create();
                    String counponStr = gson.toJson(data.getCoupon());

                    //更新
                    ContentValues values = new ContentValues();
                    values.put("point_url", data.getPoint_url());
                    values.put("point", data.getPoint());
                    values.put("pointCurrent", data.getPointCurrent());
                    values.put("couponData", counponStr);
                    values.put("backAmt", data.getBackAmt());
                    values.put("UploadFlag", false);
                    DataSupport.update(SbsPrinterData.class, values, recordInfo.getId());


                    recordInfos.remove(0);

                    upload(recordInfos);



                }

                @Override
                public void onFailure(String errorEvent, String message) {

                }

                @Override
                public void onFailurTimeOut(String s, String error_msg) {

                }

                @Override
                public void onLogin() {

                }
            });
        }








    }

//
//    private void rechargeUpload(final RechargeUpLoad rechargeUpLoad, final SbsPrinterData recordInfo) {
//        if (rechargeUpLoad == null) {
//            return;
//        }
//
//        sbsAction.rechargePay(mContext, rechargeUpLoad, new ActionCallbackListener<ChargeBlance>() {
//            @Override
//            public void onSuccess(ChargeBlance data) {
//                //更新
//                ContentValues values = new ContentValues();
//                values.put("promotion_num", rechargeUpLoad.getPromotion_num());
//                values.put("pacektRemian", data.getPacektRemian());
//                values.put("realize_card_num", data.getRealize_card_num());
//                values.put("member_name", data.getMember_name());
//                values.put("UploadFlag", false);
//                DataSupport.update(SbsPrinterData.class, values, recordInfo.getId());
//
//
//
//            }
//
//            @Override
//            public void onFailure(String errorEvent, String message) {
//
//            }
//
//            @Override
//            public void onFailurTimeOut(String s, String error_msg) {
//
//            }
//
//            @Override
//            public void onLogin() {
//
//            }
//        });
//    }
//
//
//    //流水上送
//    private void transUploadAction(final TransUploadRequest transUploadRequest, final SbsPrinterData recordInfo) {
//
//        if (transUploadRequest == null) {
//            return;
//        }
//        LogUtils.e(transUploadRequest.toString());
//
//
//        this.sbsAction.transUpload(this, transUploadRequest, new ActionCallbackListener<TransUploadResponse>() {
//            @Override
//            public void onSuccess(TransUploadResponse data) {
//
//
//                Gson gson = new GsonBuilder().serializeNulls().create();
//                String counponStr = gson.toJson(data.getCoupon());
//
//                //更新
//                ContentValues values = new ContentValues();
//                values.put("point_url", data.getPoint_url());
//                values.put("point", data.getPoint());
//                values.put("pointCurrent", data.getPointCurrent());
//                values.put("couponData", counponStr);
//                values.put("backAmt", data.getBackAmt());
//                values.put("UploadFlag", false);
//                DataSupport.update(SbsPrinterData.class, values, recordInfo.getId());
//            }
//
//            @Override
//            public void onFailure(String errorEvent, String message) {
//
//            }
//
//            @Override
//            public void onFailurTimeOut(String s, String error_msg) {
//
//            }
//
//            @Override
//            public void onLogin() {
//
//            }
//        });
//    }
//



    @Override
    protected void onDestroy() {
        super.onDestroy();
        AidlUtils.unBindService();
    }

    private void initData() {

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



}

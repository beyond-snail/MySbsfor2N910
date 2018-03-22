package com.zfsbs.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tool.utils.utils.AlertUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.R;
import com.zfsbs.core.myinterface.ActionCallbackListener;
import com.zfsbs.model.ApiResponse;
import com.zfsbs.model.VipCardNo;
import com.zfsbs.myapplication.MyApplication;


public class OpenCardActivity extends BaseActivity implements View.OnClickListener {

    private EditText etName;
    private EditText etPhone;
    private EditText etCard;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_open_card);
//        AppManager.getAppManager().addActivity(this);

        initTitle("开卡/绑卡");
        if (findViewById(R.id.add) != null) {
            findViewById(R.id.add).setVisibility(View.GONE);
            findViewById(R.id.add).setOnClickListener(this);
        }


        initView();





    }

    private ToolNewLand.DeviceListener listenser = new ToolNewLand.DeviceListener() {
        @Override
        public void success(String data) {

            final String cardNo = data;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etCard.setText(cardNo);
                    etCard.setSelection(etCard.length());
                }
            });

//
//            ToolNewLand.getToolNewLand().stopSearch();
//            ToolNewLand.getToolNewLand().searchCard(listenser);

//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(200);
//                        ToolNewLand.getToolNewLand().searchCard(listenser);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
        }

        @Override
        public void fail(final String data) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.CustomShow(mContext, data);
                }
            });
//            ToolNewLand.getToolNewLand().stopSearch();
//            ToolNewLand.getToolNewLand().searchCard(listenser);
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        Thread.sleep(200);
//                        ToolNewLand.getToolNewLand().searchCard(listenser);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).start();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        ToolNewLand.getToolNewLand().searchCard(listenser);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        ToolNewLand.getToolNewLand().stopSearch();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolNewLand.getToolNewLand().stopSearch();

    }

    private void initView() {

        etName = editText(R.id.id_member_name);
        etName.setSelection(etName.getText().length());
        etPhone = editText(R.id.id_memberphoneNo);
        etPhone.setSelection(etPhone.getText().length());
        etCard = editText(R.id.id_memberCardNo);
        etCard.setSelection(etCard.getText().length());


        button(R.id.id_bind_card).setOnClickListener(this);
        button(R.id.id_change_card).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.id_bind_card:

                openRealizeCard();
                break;
            case R.id.id_change_card:
                changeCard();
                break;
            case R.id.add:
//                CommonFunc.startAction(this, CardChangeActivity.class, false);
//                finish();

                break;
            default:
                break;
        }
    }

    private void changeCard() {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
//        String memberName = etName.getText().toString().trim();
        final String memberCard = etCard.getText().toString().trim();
        final String memberPhone = etPhone.getText().toString().trim();


        if (StringUtils.isBlank(memberPhone)) {
            ToastUtils.CustomShow(mContext, "会员手机号不为空");
            return;
        }

        if (StringUtils.isBlank(memberCard)) {
            ToastUtils.CustomShow(mContext, "会员卡号不为空");
            return;
        }

        sbsAction.changeCard(mContext, sid, memberPhone, memberCard, new ActionCallbackListener<ApiResponse<VipCardNo>>() {

            @Override
            public void onSuccess(ApiResponse<VipCardNo> response) {
                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, null, false, false, 1);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }




    private void openRealizeCard() {

        Long sid = MyApplication.getInstance().getLoginData().getSid();
        String memberName = etName.getText().toString().trim();
        final String memberCard = etCard.getText().toString().trim();
        final String memberPhone = etPhone.getText().toString().trim();


        if (StringUtils.isBlank(memberPhone)) {
            ToastUtils.CustomShow(mContext, "会员手机号不为空");
            return;
        }

        if (StringUtils.isBlank(memberCard)) {
            ToastUtils.CustomShow(mContext, "会员卡号不为空");
            return;
        }


        sbsAction.openCard(mContext, sid, memberPhone, memberCard, memberName, new ActionCallbackListener<ApiResponse<VipCardNo>>() {
            @Override
            public void onSuccess(final ApiResponse<VipCardNo> response) {

                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 2) {
                            onBackPressed();
                        } else if (response.getResult().getResCode() == 100) {
                            //确认是否绑定
                            ComfirmBindCard(memberCard, memberPhone);
                        }
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 20) {
                            onBackPressed();
                        }
                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (response.getResult().getResCode() == 20) {
                            onBackPressed();
                        }
                    }
                }, null, false, false, 1);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
//                ToolNewLand.getToolNewLand().stopSearch();
//                ToolNewLand.getToolNewLand().searchCard(listenser);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(200);
//                            ToolNewLand.getToolNewLand().searchCard(listenser);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
            }

            @Override
            public void onFailurTimeOut(String s, String error_msg) {

            }

            @Override
            public void onLogin() {

            }
        });
    }

    private void ComfirmBindCard(String memberCard, String memberPhone) {
        Long sid = MyApplication.getInstance().getLoginData().getSid();
        sbsAction.confirmBindCard(mContext, sid, memberPhone, memberCard, new ActionCallbackListener<ApiResponse<VipCardNo>>() {

            @Override
            public void onSuccess(ApiResponse<VipCardNo> response) {
                AlertUtils.alert2("提示", response.getResult().getResMsg(), mContext, "确定", "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        onBackPressed();

                    }
                }, null, false, false, 1);
            }

            @Override
            public void onFailure(String errorEvent, String message) {
                ToastUtils.CustomShow(mContext, message);
//                ToolNewLand.getToolNewLand().stopSearch();
//                ToolNewLand.getToolNewLand().searchCard(listenser);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            Thread.sleep(200);
//                            ToolNewLand.getToolNewLand().searchCard(listenser);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
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

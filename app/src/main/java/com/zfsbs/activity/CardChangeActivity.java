package com.zfsbs.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.tool.utils.utils.ToolNewLand;
import com.zfsbs.R;
import com.zfsbs.core.myinterface.ActionCallbackListener;


public class CardChangeActivity extends BaseActivity implements View.OnClickListener {

    private EditText etCardNo;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private EditText etCofimPwd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_card_change);
//        AppManager.getAppManager().addActivity(this);
        initTitle("卡密码修改");

        initView();

    }

    private void initView() {
        etCardNo = editText(R.id.id_memberCardNo);
        etOldPwd = editText(R.id.id_old_pwd);
        etNewPwd = editText(R.id.id_new_psw);
        etCofimPwd = editText(R.id.id_comfirm_psw);

        button(R.id.id_ok).setOnClickListener(this);




    }

    private ToolNewLand.DeviceListener listenser = new ToolNewLand.DeviceListener() {
        @Override
        public void success(String data) {
            final String cardNo = data;
            ((Activity) mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    etCardNo.setText(cardNo);
                    etCardNo.setSelection(etCardNo.length());
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
    };


    @Override
    protected void onResume() {
        super.onResume();
        ToolNewLand.getToolNewLand().searchCard(listenser);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ToolNewLand.getToolNewLand().stopSearch();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.id_ok:

                if (StringUtils.isBlank(etCardNo.getText().toString().trim())){
                    ToastUtils.CustomShow(mContext, "会员卡号为空");
                    return;
                }
                if (StringUtils.isBlank(etOldPwd.getText().toString().trim())){
                    ToastUtils.CustomShow(mContext, "原支付密码为空");
                    return;
                }
                if (StringUtils.isBlank(etNewPwd.getText().toString().trim())){
                    ToastUtils.CustomShow(mContext, "新支付密码为空");
                    return;
                }
                if (StringUtils.isBlank(etCofimPwd.getText().toString().trim())){
                    ToastUtils.CustomShow(mContext, "确认支付密码为空");
                    return;
                }

                if (!StringUtils.isEquals(etNewPwd.getText().toString().trim(), etCofimPwd.getText().toString().trim())){
                    ToastUtils.CustomShow(mContext, "两次密码不一致");
                    return;
                }

                String cardNo = etCardNo.getText().toString().trim();
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                changePass(mContext, cardNo, newPwd, oldPwd);

                break;
            default:
                break;
        }
    }

    private void changePass(final Context mContext, String cardNo, String newPwd, String oldPwd) {

        sbsAction.changePass(mContext, cardNo, newPwd, oldPwd, new ActionCallbackListener<String>() {
            @Override
            public void onSuccess(String data) {
                ToastUtils.CustomShow(mContext, data);
                onBackPressed();
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
}

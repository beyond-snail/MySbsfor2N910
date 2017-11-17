package com.zfsbs.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.NetUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.common.CommonFunc;
import com.zfsbs.core.action.LoginAction;
import com.zfsbs.core.myinterface.UiAction;


public class GetLoginInfoActivity1 extends BaseActivity implements OnClickListener{


    private static final String TAG = "GetLoginInfoActivity1";

    private Button btnGetInfo;
    private EditText edPassWord;
    private EditText edUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_info_get1);
//        AppManager.getAppManager().addActivity(this);
        initTitle("签到");

        btnGetInfo = (Button) findViewById(R.id.id_info_get);
        edPassWord = (EditText) findViewById(R.id.password);
        edUserName = (EditText) findViewById(R.id.id_username);

        edUserName.setText("");
        edUserName.setSelection(edPassWord.getText().length());

        btnGetInfo.setOnClickListener(this);
    }

    private void getInfo(String username, String password) {
        LoginAction loginAction = new LoginAction(GetLoginInfoActivity1.this, new UiAction() {

            @Override
            public void UiResultAction(Activity context, Class<?> cls, Bundle bundle, int requestCode) {
                LogUtils.e("UiResultAction01");
            }

            @Override
            public void UiAction(Activity context, Class<?> cls, Bundle bundle, boolean flag) {
                LogUtils.e("UiAction01");
            }

            @Override
            public void UiAction(Activity context, Class<?> cls, boolean flag) {
                LogUtils.e("UiAction02");
                finish();
//                CommonFunc.startAction(context, cls, flag);
            }
        });

        loginAction.loginAction(username, password);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_info_get:
                get();
                break;

            default:
                break;
        }
    }

    private void get() {
        if (NetUtils.isConnected(this)) {
            if (TextUtils.isEmpty(edUserName.getText().toString())) {
                ToastUtils.CustomShow(this, "请输入操作员号");
                return;
            }
            if (TextUtils.isEmpty(edPassWord.getText().toString())) {
                ToastUtils.CustomShow(this, "请输入操作员密码");
                return;
            }

            getInfo(edUserName.getText().toString(), edPassWord.getText().toString());


        } else {
            ToastUtils.CustomShow(this, "请打开网络");
        }

    }


}

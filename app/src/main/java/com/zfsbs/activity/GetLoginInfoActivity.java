package com.zfsbs.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.tool.utils.utils.LogUtils;
import com.tool.utils.utils.NetUtils;
import com.tool.utils.utils.SPUtils;
import com.tool.utils.utils.StringUtils;
import com.tool.utils.utils.ToastUtils;
import com.zfsbs.R;
import com.zfsbs.config.Constants;
import com.zfsbs.core.action.LoginAction;
import com.zfsbs.core.myinterface.UiAction;


public class GetLoginInfoActivity extends BaseActivity implements OnClickListener, OnTouchListener {


    private static final String TAG = "GetLoginInfoActivity";

    private Button btnGetInfo;
    private EditText edPassWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_get);
//        AppManager.getAppManager().addActivity(this);

        btnGetInfo = (Button) findViewById(R.id.id_info_get);
        edPassWord = (EditText) findViewById(R.id.password);

        edPassWord.setText("");
        edPassWord.setSelection(edPassWord.getText().length());

        btnGetInfo.setOnClickListener(this);
    }

    private void getInfo() {
        LoginAction loginAction = new LoginAction(GetLoginInfoActivity.this, new UiAction() {

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

            }
        });

        loginAction.loginAction();
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
        String pass = (String) SPUtils.get(this, Constants.MASTER_PASS, Constants.DEFAULT_MASTER_PASS);
        if (NetUtils.isConnected(this)) {
            if (TextUtils.isEmpty(edPassWord.getText().toString())) {
                ToastUtils.CustomShow(this, "请输入密码");
                getFocus();
                return;
            }
            if (StringUtils.isEquals(pass, edPassWord.getText().toString())) {
                getInfo();
            } else {
                ToastUtils.CustomShow(this, "密码错误");
                getFocus();
            }

        } else {
            ToastUtils.CustomShow(this, "请打开网络");
            getFocus();
        }

    }

    private void getFocus() {
        edPassWord.postDelayed(new Runnable() {

            @Override
            public void run() {
                edPassWord.setFocusable(true);
                edPassWord.setFocusableInTouchMode(true);
                edPassWord.requestFocus();
                edPassWord.findFocus();
            }
        }, 500);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edPassWord.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        getFocus();
        return true;
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            get();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}

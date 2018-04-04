package com.zfsbs.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.model.SbsPrinterData;
import com.tool.utils.activityManager.AppManager;
import com.tool.utils.utils.WifiChangeBroadcastReceiver;
import com.zfsbs.R;
import com.zfsbs.core.action.SbsAction;
import com.zfsbs.myapplication.MyApplication;
import com.zfsbs.service.LoginReceiver;


import java.util.Calendar;
import java.util.TimeZone;


public abstract class BaseActivity extends Activity {

    private static final String TAG = "BaseActivity";


    // 上下文实例
    protected Context mContext;
    // 应用全局的实例
    protected MyApplication application;
    // 核心层的Action实例
//	protected EmvImpl emvImpl;
    public SbsAction sbsAction;
//    protected Printer printer;
    protected SbsPrinterData printerData;


    private WifiChangeBroadcastReceiver receiver;


    public BaseActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        application = (MyApplication) this.getApplication();
//		emvImpl = application.getEmvImpl();
        sbsAction = application.getSbsAction();
        printerData = new SbsPrinterData();
//        printer = Printer.getInstance(this);


        //获取广播对象
        receiver = new WifiChangeBroadcastReceiver();
        //创建意图过滤器
        IntentFilter filter = new IntentFilter();
        //添加动作，监听网络
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);


        //封装一个定时器用于登录
        setAlarmToLogin();
    }


    /**
     * 初始化标题和回退
     */
    public void initTitle(String title) {
        if (textView(R.id.tv_header) != null) {
            textView(R.id.tv_header).setText(title);
        }
        if (findViewById(R.id.backBtn) != null) {
            findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 触发手机返回按钮
     */
    @Override
    public void onBackPressed() {

        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        AppManager.getAppManager().finishActivity(BaseActivity.this);
    }




    /**
     * 文本View
     */
    public TextView textView(int textview) {
        return (TextView) findViewById(textview);
    }

    protected void setTvText(int vid, String text) {
        TextView view = (TextView) findViewById(vid);
        if(view!=null){view.setText(TextUtils.isEmpty(text) ? "": text);}
    }

    protected void showView(int vid, boolean isShow){
        View view = (View) findViewById(vid);
        if(view!=null){
            view.setVisibility(isShow?View.VISIBLE:View.GONE);
        }
    }

    protected void showLayoutView(int vid, boolean isShow){
        LinearLayout view = (LinearLayout) findViewById(vid);
        if(view!=null){
            view.setVisibility(isShow?View.VISIBLE:View.GONE);
        }
    }

    protected void setTvEnable(int vid, boolean boo){
        View view = findViewById(vid);
        if(view!=null){view.setEnabled(boo);}
    }
    /**
     * 文本button
     */
    public Button button(int id) {
        return (Button) findViewById(id);
    }

    /**
     * 文本button
     */
    public ImageView imageView(int id) {
        return (ImageView) findViewById(id);
    }

    /**
     * 文本editText
     */
    public EditText editText(int id) {
        return (EditText) findViewById(id);
    }

    public LinearLayout linearLayout(int id) {
        return (LinearLayout) findViewById(id);
    }

    public RelativeLayout rinearLayout(int id) {
        return (RelativeLayout) findViewById(id);
    }

    private void setAlarmToLogin() {
        Intent intent = new Intent(BaseActivity.this, LoginReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(BaseActivity.this, 0, intent, 0);

        long firstTime = SystemClock.elapsedRealtime();    // 开机之后到现在的运行时间(包括睡眠时间)
        long systemTime = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 选择的每天定时时间
        long selectTime = calendar.getTimeInMillis();

        // 如果当前时间大于设置的时间，那么就从第二天的设定时间开始
        if (systemTime > selectTime) {
//            Toast.makeText(BaseActivity.this, "设置的时间小于当前时间", Toast.LENGTH_SHORT).show();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }

        // 计算现在时间到设定时间的时间差
        long time = selectTime - systemTime;
        firstTime += time;

        long DAY = 1000L * 60 * 60 * 24;



        // 进行闹铃注册
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                firstTime, DAY, sender);

        Log.e(TAG, "time ==== " + time + ", selectTime ===== "
                + selectTime + ", systemTime ==== " + systemTime + ", firstTime === " + firstTime);

//        Toast.makeText(BaseActivity.this, "设置重复闹铃成功! ", Toast.LENGTH_LONG).show();
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
    }
}

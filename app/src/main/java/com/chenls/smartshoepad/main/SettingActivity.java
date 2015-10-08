package com.chenls.smartshoepad.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.chenls.smartshoepad.R;
import com.chenls.smartshoepad.setting.Choose;
import com.chenls.smartshoepad.setting.Input;
import com.chenls.smartshoepad.welcome.SetActivity;

public class SettingActivity extends Activity {
    private static final int REQUEST_AUTO_CONNECT = 2;
    public static final String AUTO_CONNECT = "isAutoConnect";
    public static final String SURE_PSD = "surePSD";
    public static final String SAFE_SET = "safeSet";
    public static final String FALL_SET = "fallSet";
    private static final int REQUEST_SAFE_SET = 3;
    private static final int REQUEST_FALL_SET = 4;
    private TextView personalSet, autoConnect, isAutoConnect, safeSet, safe, fallSet, fall;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);
        safeSet = (TextView) findViewById(R.id.safeSet);
        safe = (TextView) findViewById(R.id.safe);
        fallSet = (TextView) findViewById(R.id.fallSet);
        fall = (TextView) findViewById(R.id.fall);
        personalSet = (TextView) findViewById(R.id.personalSet);
        autoConnect = (TextView) findViewById(R.id.autoConnect);
        isAutoConnect = (TextView) findViewById(R.id.isAutoConnect);
        personalSet.setOnClickListener(new OnClickListener());
        autoConnect.setOnClickListener(new OnClickListener());
        safeSet.setOnClickListener(new OnClickListener());
        fallSet.setOnClickListener(new OnClickListener());
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
        }
        if (sharedPreferences.getBoolean(Choose.IS_AUTO_CONNECT, false)) {
            isAutoConnect.setText(R.string.yes);
        } else {
            isAutoConnect.setText(R.string.no);
        }

        if (sharedPreferences.getBoolean(WarningSetActivity.SAFE, false)) {
            safe.setText(R.string.open);
        } else {
            safe.setText(R.string.close);
        }

        if (sharedPreferences.getBoolean(WarningSetActivity.FALL, false)) {
            fall.setText(R.string.open);
        } else {
            fall.setText(R.string.close);
        }
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == personalSet) {
                Intent newIntent = new Intent(SettingActivity.this, SetActivity.class);
                startActivity(newIntent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == autoConnect) {
                Intent newIntent = new Intent(SettingActivity.this, Choose.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(AUTO_CONNECT, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_AUTO_CONNECT);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == safeSet) {
                Intent newIntent = new Intent(SettingActivity.this, WarningSetActivity.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(SAFE_SET, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_SAFE_SET);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == fallSet) {
                Intent newIntent = new Intent(SettingActivity.this, WarningSetActivity.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(FALL_SET, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_FALL_SET);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {


            case REQUEST_AUTO_CONNECT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(Choose.CHOOSE_RESULT);
                    if ((Choose.NO).equals(result)) {
                        isAutoConnect.setText(getString(R.string.no));
                    } else if ((Choose.YES).equals(result)) {
                        isAutoConnect.setText(getString(R.string.yes));
                    }
                }
                break;
            case REQUEST_SAFE_SET:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(WarningSetActivity.RESULT);
                    safe.setText(result);
                }
                break;
            case REQUEST_FALL_SET:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(WarningSetActivity.RESULT);
                    fall.setText(result);
                }
                break;
            default:
                break;
        }
    }

    private void closeSetting() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            closeSetting();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) { //监控/拦截菜单键
            closeSetting();
            return true;
        } else
            return super.onKeyDown(keyCode, event);
    }
}

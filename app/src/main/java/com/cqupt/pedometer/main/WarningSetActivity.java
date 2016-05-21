package com.cqupt.pedometer.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cqupt.pedometer.R;
import com.cqupt.pedometer.setting.Choose;
import com.cqupt.pedometer.setting.Input;

public class WarningSetActivity extends Activity {
    public static final String RESULT = "result";
    public static final String MY_SWITCH = "mySwitchSet";
    private static final int REQUEST_MY_SWITCH = 1;
    public static final String PHONE_SET = "phoneSet";
    private static final int REQUEST_PHONE_SET = 2;
    public static final String MESSAGE_SET = "messageSet";
    private static final int REQUEST_MESSAGE_SET = 3;
    public static final String SAFE = "safe";
    public static final String FALL = "fall";
    private SharedPreferences sharedPreferences;
    private TextView mySwitchSet, mySwitch, phoneSet, phone, messageSet, message, title;
    private boolean isFallSet, isSafeSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_warning_set);
        mySwitch = (TextView) findViewById(R.id.mySwitch);
        mySwitchSet = (TextView) findViewById(R.id.mySwitchSet);
        phoneSet = (TextView) findViewById(R.id.phoneSet);
        phone = (TextView) findViewById(R.id.phone);
        messageSet = (TextView) findViewById(R.id.messageSet);
        message = (TextView) findViewById(R.id.message);
        mySwitchSet.setOnClickListener(new OnClickListener());
        phoneSet.setOnClickListener(new OnClickListener());
        messageSet.setOnClickListener(new OnClickListener());
        title = (TextView) findViewById(R.id.title);
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
        //跌倒设置
        try {
            isFallSet = bundle.getBoolean(SettingActivity.FALL_SET);
            if (isFallSet) {
                SetUI(getString(R.string.fallSet), WarningSetActivity.FALL,
                        sharedPreferences.getString(WarningSetActivity.FALL + "Phone", null),
                        sharedPreferences.getString(WarningSetActivity.FALL + "Message", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //安全设置
        try {
            isSafeSet = bundle.getBoolean(SettingActivity.SAFE_SET);
            if (isSafeSet) {
                SetUI(getString(R.string.safeSet), WarningSetActivity.SAFE,
                        sharedPreferences.getString(WarningSetActivity.SAFE + "Phone", null),
                        sharedPreferences.getString(WarningSetActivity.SAFE + "Message", null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetUI(String string, String fall, String string2, String string3) {
        title.setText(string);
        if (sharedPreferences.getBoolean(fall, false)) {
            mySwitch.setText(R.string.open);
        } else {
            mySwitch.setText(R.string.close);
        }
        if (!TextUtils.isEmpty(string2)) {
            phone.setText(string2);
        }
        if (!TextUtils.isEmpty(string3)) {
            message.setText(string3);
        }
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == mySwitchSet) {
                Intent newIntent = new Intent(WarningSetActivity.this, Choose.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(MY_SWITCH, true);     // 标示是SET_GENDER 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_MY_SWITCH);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == phoneSet) {
                Intent newIntent = new Intent(WarningSetActivity.this, Input.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(Input.PHONE_SET, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_PHONE_SET);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == messageSet) {
                Intent newIntent = new Intent(WarningSetActivity.this, Input.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(Input.MESSAGE_SET, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_MESSAGE_SET);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MY_SWITCH:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(WarningSetActivity.RESULT);
                    mySwitch.setText(result);
                }
                break;
            case REQUEST_PHONE_SET:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(WarningSetActivity.RESULT);
                    phone.setText(result);
                }
                break;
            case REQUEST_MESSAGE_SET:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(WarningSetActivity.RESULT);
                    message.setText(result);
                }
                break;
            default:
                break;
        }
    }

    public void titleImageButton(View view) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void sure(View view) {
        String key = "";
        if (isSafeSet) {
            key = SAFE;
        } else if (isFallSet) {
            key = FALL;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean switchValue = false;
        if (mySwitch.getText().toString().equals(getString(R.string.open))) {
            switchValue = true;
        }
        editor.putBoolean(key, switchValue);
        editor.putString(key + "Phone", phone.getText().toString());
        editor.putString(key + "Message", message.getText().toString());
        editor.commit();
        finishAndPutData(mySwitch.getText().toString());
    }

    private void finishAndPutData(String s) {
        Bundle b = new Bundle();
        b.putString(RESULT, s);
        Intent result = new Intent();
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

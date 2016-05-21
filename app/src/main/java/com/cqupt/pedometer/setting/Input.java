package com.cqupt.pedometer.setting;

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
import android.widget.EditText;
import android.widget.TextView;

import com.cqupt.pedometer.R;
import com.cqupt.pedometer.main.CommonTools;
import com.cqupt.pedometer.main.WarningSetActivity;
import com.cqupt.pedometer.welcome.SetActivity;

public class Input extends Activity {
    public static final String MY_DATA = "myDate";
    public static final String SET_HEIGHT = "setHeight";
    public static final String SET_WEIGHT = "setWeight";
    public static final String PHONE_SET = "phoneSet";
    public static final String MESSAGE_SET = "messageSet";
    private SharedPreferences sharedPreferences;
    private boolean  isSetHeight, isSetWeight, isPhoneSet, isMessageSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);

        try {
            sharedPreferences = this.getSharedPreferences(MY_DATA,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
                try {
            isSetHeight = bundle.getBoolean(SetActivity.SET_HEIGHT);
            if (isSetHeight) {
                ((TextView) findViewById(R.id.title)).setText(getString(R.string.set_height));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isSetWeight = bundle.getBoolean(SetActivity.SET_WEIGHT);
            if (isSetWeight) {
                ((TextView) findViewById(R.id.title)).setText(getString(R.string.set_weight));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isPhoneSet = bundle.getBoolean(WarningSetActivity.PHONE_SET);
            if (isPhoneSet) {
                ((TextView) findViewById(R.id.title)).setText(getString(R.string.phone));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isMessageSet = bundle.getBoolean(WarningSetActivity.MESSAGE_SET);
            if (isMessageSet) {
                ((TextView) findViewById(R.id.title)).setText(getString(R.string.message));
                ((EditText) findViewById(R.id.et_pwd)).setInputType(0x00000001);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void myFinish() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void titleImageButton(View view) {
        myFinish();
    }

    public void cancel(View view) {
        myFinish();
    }

    public void sure(View view) {
        EditText et_pwd;
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        String value = et_pwd.getText().toString();
        if (isPhoneSet) {
            finishAndPutData(value, WarningSetActivity.RESULT);
            return;
        } else if (isMessageSet) {
            finishAndPutData(value, WarningSetActivity.RESULT);
            return;
        } else if (isSetWeight) {
            finishAndSaveData(value, SET_WEIGHT);
            return;
        } else if (isSetHeight) {
            finishAndSaveData(value, SET_HEIGHT);
            return;
        }

    }

    private void finishAndSaveData(String value, String setWeight) {
        if (TextUtils.isEmpty(value)) {
            CommonTools.showShortToast(this, getString(R.string.input_is_null));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(setWeight, value);
            editor.commit();
            myPutData(value, setWeight);
        }
    }

    private void finishAndPutData(String value, String messageSet) {
        if (TextUtils.isEmpty(value)) {
            CommonTools.showShortToast(this, getString(R.string.input_is_null));
        } else {
            myPutData(value, messageSet);
        }
    }

    private void myPutData(String value, String messageSet) {
        Bundle b = new Bundle();
        b.putString(messageSet, value);
        Intent result = new Intent();
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

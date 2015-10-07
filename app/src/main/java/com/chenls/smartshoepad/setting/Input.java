package com.chenls.smartshoepad.setting;

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

import com.chenls.smartshoepad.R;
import com.chenls.smartshoepad.main.CommonTools;
import com.chenls.smartshoepad.main.SettingActivity;
import com.chenls.smartshoepad.welcome.SetActivity;

public class Input extends Activity {
    public static final String IS_NEED_PSD = "isNeedPSD";
    public static final String PSD = "passWord";
    public static final String MY_DATA = "myDate";
    public static final String NUM_PSD = "numPsd";
    public static final String CHANGE_NAME = "changName";
    public static final String SET_HEIGHT = "setHeight";
    public static final String SET_WEIGHT = "setWeight";
    private SharedPreferences sharedPreferences;
    private boolean isSurePSD, isSetHeight, isSetWeight;

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
            isSurePSD = bundle.getBoolean(SettingActivity.SURE_PSD);
            if (isSurePSD) {
                ((TextView) findViewById(R.id.title)).setText(R.string.sure_psd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        if (isSetWeight) {
            if (TextUtils.isEmpty(value)) {
                CommonTools.showShortToast(this, getString(R.string.name_is_null));
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SET_WEIGHT, value);
                editor.commit();
                Bundle b = new Bundle();
                b.putString(SET_WEIGHT, value);
                Intent result = new Intent();
                result.putExtras(b);
                setResult(Activity.RESULT_OK, result);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return;
        } else if (isSetHeight) {
            if (TextUtils.isEmpty(value)) {
                CommonTools.showShortToast(this, getString(R.string.name_is_null));
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SET_HEIGHT, value);
                editor.commit();
                Bundle b = new Bundle();
                b.putString(SET_HEIGHT, value);
                Intent result = new Intent();
                result.putExtras(b);
                setResult(Activity.RESULT_OK, result);
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return;
        } else if (isSurePSD) {
            if (!TextUtils.isEmpty(value)) {
                String passWord = sharedPreferences.getString(Input.PSD, null);
                if (value.equals(passWord)) {
                    Intent newIntent = new Intent(Input.this, Choose.class);
                    startActivity(newIntent);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    et_pwd.setText("");
                    CommonTools.showShortToast(Input.this, getString(R.string.psd_error));
                }
            } else {
                CommonTools.showShortToast(Input.this, getString(R.string.PSD_is_null));
            }
        } else if (TextUtils.isEmpty(value)) {
            CommonTools.showShortToast(this, getString(R.string.PSD_is_null));
        } else if (value.length() < 4 || value.length() > 16) {
            CommonTools.showShortToast(this, getString(R.string.PSD_is_less_than_four));
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(IS_NEED_PSD, true);
            editor.putString(PSD, value);
            editor.commit();

            finishAndPutData(NUM_PSD);
        }
    }

    private void finishAndPutData(String changeName) {
        Bundle b = new Bundle();
        b.putBoolean(changeName, true);
        Intent result = new Intent();
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

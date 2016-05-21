package com.cqupt.pedometer.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cqupt.pedometer.R;
import com.cqupt.pedometer.main.SettingActivity;
import com.cqupt.pedometer.main.WarningSetActivity;
import com.cqupt.pedometer.welcome.SetActivity;

public class Choose extends Activity {
    public static final String CHOOSE_RESULT = "result";
    public static final String NO = "no";
    public static final String YES = "yes";
    public static final String IS_AUTO_CONNECT = "isAutoConnect";
    public static final String IS_MANUAL_SET_NOT_AUTO_CONNECT = "isManual";
    public static final String GENDER = "gender";
    private TextView title, isTrue, isFalse;
    private boolean isAutoConnect, isSetGender,isMySwitch;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose);
        title = (TextView) findViewById(R.id.title);
        isTrue = (TextView) findViewById(R.id.isTrue);
        isFalse = (TextView) findViewById(R.id.isFalse);
        title.setOnClickListener(new OnClickListener());
        isTrue.setOnClickListener(new OnClickListener());
        isFalse.setOnClickListener(new OnClickListener());
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
        try {
            isAutoConnect = bundle.getBoolean(SettingActivity.AUTO_CONNECT);
            if (isAutoConnect) {
                title.setText(getString(R.string.autoConnect));
                isTrue.setText(getString(R.string.yes));
                isFalse.setText(getString(R.string.no));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isSetGender = bundle.getBoolean(SetActivity.SET_GENDER);
            if (isSetGender) {
                title.setText(getString(R.string.choose_gender));
                isTrue.setText(getString(R.string.female));
                isFalse.setText(getString(R.string.male));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            isMySwitch = bundle.getBoolean(WarningSetActivity.MY_SWITCH);
            if (isMySwitch) {
                title.setText(getString(R.string.mySwitch));
                isTrue.setText(getString(R.string.close));
                isFalse.setText(getString(R.string.open));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void titleImageButton(View view) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (isMySwitch) {
                if (v == isTrue) {
                    finishAndPutData(getString(R.string.close));
                } else if (v == isFalse) {
                    finishAndPutData(getString(R.string.open));
                }
                return;
            }
            if (isSetGender) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (v == isTrue) {
                    editor.putString(GENDER, getString(R.string.female));
                    finishAndPutData(getString(R.string.female));
                } else if (v == isFalse) {
                    editor.putString(GENDER, getString(R.string.male));
                    finishAndPutData(getString(R.string.male));
                }
                editor.commit();
                return;
            }
            if (isAutoConnect) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (v == isTrue) {
                    editor.putBoolean(IS_AUTO_CONNECT, true);
                    editor.putBoolean(IS_MANUAL_SET_NOT_AUTO_CONNECT, false);
                    finishAndPutData(YES);
                } else if (v == isFalse) {
                    editor.putBoolean(IS_AUTO_CONNECT, false);
                    editor.putBoolean(IS_MANUAL_SET_NOT_AUTO_CONNECT, true);
                    finishAndPutData(NO);
                }
                editor.commit();
                return;
            }
        }
    }

    private void finishAndPutData(String noPsd) {
        Bundle b = new Bundle();
        b.putString(CHOOSE_RESULT, noPsd);
        Intent result = new Intent();
        result.putExtras(b);
        setResult(Activity.RESULT_OK, result);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

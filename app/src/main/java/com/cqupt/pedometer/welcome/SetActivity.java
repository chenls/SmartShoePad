package com.cqupt.pedometer.welcome;

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
import com.cqupt.pedometer.setting.Choose;
import com.cqupt.pedometer.setting.Input;

public class SetActivity extends Activity {
    private static final int REQUEST_SET_GENDER = 1;
    public static final String SET_GENDER = "setGender";
    public static final String SET_HEIGHT = "setHeight";
    private static final int REQUEST_SET_HEIGHT = 2;
    public static final String SET_WEIGHT = "setWeight";
    private static final int REQUEST_SET_WEIGHT = 3;
    private SharedPreferences sharedPreferences;
    private TextView setGender, gender, setHeight, height, setWeight, weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_set);
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA, Context.MODE_PRIVATE);
        } catch (Exception e) {
        }
        setGender = (TextView) findViewById(R.id.setGender);
        gender = (TextView) findViewById(R.id.gender);
        setHeight = (TextView) findViewById(R.id.setHeight);
        height = (TextView) findViewById(R.id.height);
        setWeight = (TextView) findViewById(R.id.setWeight);
        weight = (TextView) findViewById(R.id.weight);
        gender.setText(sharedPreferences.getString(Choose.GENDER, getString(R.string.male)));
        height.setText(sharedPreferences.getString(SET_HEIGHT, "160") + "CM");
        weight.setText(sharedPreferences.getString(SET_WEIGHT, "50") + "KG");
        setGender.setOnClickListener(new OnClickListener());
        setHeight.setOnClickListener(new OnClickListener());
        setWeight.setOnClickListener(new OnClickListener());
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == setGender) {
                Intent newIntent = new Intent(SetActivity.this, Choose.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(SET_GENDER, true);     // 标示是SET_GENDER 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_SET_GENDER);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == setHeight) {
                Intent newIntent = new Intent(SetActivity.this, Input.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(SET_HEIGHT, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_SET_HEIGHT);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else if (v == setWeight) {
                Intent newIntent = new Intent(SetActivity.this, Input.class);
                Bundle bundle = new Bundle(); //创建Bundle对象
                bundle.putBoolean(SET_WEIGHT, true);     // 标示是autoConnect 启动的新Activity
                newIntent.putExtras(bundle);
                startActivityForResult(newIntent, REQUEST_SET_WEIGHT);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_SET_GENDER:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(Choose.CHOOSE_RESULT);
                    if ((getString(R.string.male)).equals(result)) {
                        gender.setText(getString(R.string.male));
                    } else if ((getString(R.string.female)).equals(result)) {
                        gender.setText(getString(R.string.female));
                    }
                }
                break;
            case REQUEST_SET_HEIGHT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(Input.SET_HEIGHT);
                    height.setText(result + "CM");

                }
                break;
            case REQUEST_SET_WEIGHT:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String result = data.getStringExtra(Input.SET_WEIGHT);
                    weight.setText(result + "KG");

                }
                break;
            default:
                break;
        }
    }

    public void titleImageButton(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSet", true);
        editor.commit();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void sure(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSet", true);
        editor.putString(Choose.GENDER, gender.getText().toString());
        String h = height.getText().toString();
        editor.putString(SET_HEIGHT, (String) h.subSequence(0, h.length() - 2));
        String w = weight.getText().toString();
        editor.putString(SET_WEIGHT, (String) w.subSequence(0, w.length() - 2));
        editor.commit();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

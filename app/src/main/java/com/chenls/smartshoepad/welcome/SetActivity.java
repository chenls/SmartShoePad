package com.chenls.smartshoepad.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.chenls.smartshoepad.R;
import com.chenls.smartshoepad.setting.Input;

public class SetActivity extends Activity {
    private SharedPreferences sharedPreferences;

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
    }

    public void titleImageButton(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isSet", true);
        editor.commit();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void sure(View view) {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

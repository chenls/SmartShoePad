package com.cqupt.pedometer.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cqupt.pedometer.R;
import com.cqupt.pedometer.main.WelcomeActivity;
import com.cqupt.pedometer.setting.Input;

import java.util.ArrayList;


public class PageViewActivity extends Activity {
    //定义视图
    private View view1, view2, view3;
    private Button bn;
    private ViewPager viewPager;
    Intent intent;
    //圆点图标数组
    private ImageView[] img;
    //视图列表
    ArrayList<View> viewList;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA, Context.MODE_PRIVATE);
            //是否进入滑动欢迎页
            if (sharedPreferences.getBoolean("isFirst", false)) {
                Intent intent = new Intent(PageViewActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } catch (Exception e) {
        }
        setContentView(R.layout.activity_page_view);
        //定义选项卡、
        LayoutInflater lf = getLayoutInflater().from(this);
        view1 = lf.inflate(R.layout.demo1, null);
        view2 = lf.inflate(R.layout.demo2, null);
        view3 = lf.inflate(R.layout.demo3, null);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewList = new ArrayList<View>();// 将要分页显示的View装入数组中
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        img = new ImageView[viewList.size()];
        LinearLayout layout = (LinearLayout) findViewById(R.id.viewGroup);
        for (int i = 0; i < viewList.size(); i++) {
            img[i] = new ImageView(PageViewActivity.this);
            if (0 == i) {
                img[i].setBackgroundResource(R.mipmap.dot1);
            } else {
                img[i].setBackgroundResource(R.mipmap.dot2);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 10, 0);
            img[i].setLayoutParams(params);
            layout.addView(img[i]);
        }
        //定义适配器
        PagerAdapter pager = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewList.get(position));

            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(viewList.get(position));

                return viewList.get(position);
            }

        };
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                // TODO Auto-generated method stub
                for (int i = 0; i < viewList.size(); i++) {
                    if (arg0 == i) {
                        img[i].setBackgroundResource(R.mipmap.dot1);
                    } else {
                        img[i].setBackgroundResource(R.mipmap.dot2);
                    }
                }
                if (arg0 == viewList.size() - 1) {
                    bn = (Button) findViewById(R.id.bn1);
                    bn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isFirst", true);
                            editor.commit();
                            intent = new Intent(PageViewActivity.this, WelcomeActivity.class);
                            PageViewActivity.this.startActivity(intent);
                            PageViewActivity.this.finish();
                        }
                    });
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }
        });
        viewPager.setAdapter(pager);
    }
}

package com.chenls.smartshoepad.main;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.chenls.smartshoepad.R;
import com.chenls.smartshoepad.setting.Choose;
import com.chenls.smartshoepad.setting.Input;
import com.chenls.smartshoepad.welcome.SetActivity;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends Activity {
    private static final int REQUEST_CHANGE_NAME = 2;
    private int TIME = 2000;
    private TextView step, distance, calorie, tv_bluetooth_name, tv_battery, tv_rssi, time, timeAnimation;
    private RelativeLayout notTime;
    private Button connect, setting, cancelButton;
    private BluetoothDevice mDevice = null;
    private static final int UART_PROFILE_DISCONNECTED = 21;
    private int mState = UART_PROFILE_DISCONNECTED;
    private UartService mService = null;
    private String deviceAddress;
    private static final int UART_PROFILE_CONNECTED = 20;
    private SharedPreferences sharedPreferences;
    private String rssi;
    private boolean isManualDisconnect;
    private Animation myAnimation_Scale;
    private BMapManager mapManager;
    private MKLocationManager locationManager;
    private Vibrator mVibrator01; // 声明一个振动器对象
    private boolean isSafe, isCancel, nowIsWarning;
    private TimeCount timeCount;
    static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String senderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        Intent intent = this.getIntent();        //获取已有的intent对象
        Bundle bundle = intent.getExtras();    //获取intent里面的bundle对象
        deviceAddress = bundle.getString(BluetoothDevice.EXTRA_DEVICE);
        rssi = bundle.getString("rssi");
        tv_rssi = ((TextView) findViewById(R.id.tv_rssi));
        mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        service_init();
        tv_bluetooth_name = (TextView) findViewById(R.id.tv_bluetooth_name);
        tv_bluetooth_name.setText(getString(R.string.bluetooth_name) + mDevice.getName());
        tv_battery = (TextView) findViewById(R.id.tv_battery);
        step = (TextView) findViewById(R.id.step);
        distance = (TextView) findViewById(R.id.distance);
        calorie = (TextView) findViewById(R.id.calorie);
        connect = (Button) findViewById(R.id.connect);
        setting = (Button) findViewById(R.id.setting);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        time = (TextView) findViewById(R.id.time);
        timeAnimation = (TextView) findViewById(R.id.timeAnimation);
        notTime = (RelativeLayout) findViewById(R.id.notTime);
        cancelButton.setOnClickListener(new OnClickListener());
        connect.setOnClickListener(new OnClickListener());
        setting.setOnClickListener(new OnClickListener());
        handler.postDelayed(runnable, TIME); //每隔2s执行
        try {
            sharedPreferences = this.getSharedPreferences(Input.MY_DATA,
                    Context.MODE_PRIVATE);
        } catch (Exception e) {
        }
        //动态注册广播消息
        IntentFilter intentFilter = new IntentFilter(SMS_RECEIVED);
        intentFilter.setPriority(1000);
        registerReceiver(SMSBroadcastReceiver, intentFilter);
    }

    /**
     * 短信监听
     */
    private BroadcastReceiver SMSBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            SmsMessage msg;
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object p : pdusObj) {
                    msg = SmsMessage.createFromPdu((byte[]) p);
                    String msgTxt = msg.getMessageBody();//得到消息的内容
                    senderNumber = msg.getOriginatingAddress();
                    if (msgTxt.contains("address")) {
//                    if (true) {
                        CommonTools.showShortToast(MainActivity.this, "收到短信了");
                        //如果流量未开启，则开启数据流量
                        toggleMobileData(MainActivity.this, true);
                        //定位
                        getPosition();
                        //5秒后发送短信
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // 延时5秒开始发送短信
                                SmsManager manager = SmsManager.getDefault();
                                ArrayList<String> texts = manager.divideMessage(tv2);
                                try {
                                    manager.sendMultipartTextMessage(senderNumber
                                            , null, texts, null, null);
                                } catch (Exception e) {
                                }
                            }
                        }, 5000);
                        return;
                    }
                }
            }
        }
    };

    private void getPosition() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                // 延时4秒开始定位
                mapManager = new BMapManager(MainActivity.this); // 开始获取定位信息定位
                locationManager = mapManager.getLocationManager();
                mapManager.init(getString(R.string.baiduKey), new MyMKGeneralListener());
                locationManager.setNotifyInternal(20, 5);
                locationManager.requestLocationUpdates(new MyLocationListener());
                mapManager.start();
            }
        }, 4000);
    }

    public class OnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v == connect) {
                if (connect.getText().equals(getString(R.string.connect))) {
                    CommonTools.showShortToast(MainActivity.this, getString(R.string.tryReconnet));
                    mService.connect(deviceAddress);
                } else {
                    if (mDevice != null) {
                        mService.disconnect();
                    }
                    skipWelcomeActivity();
                }
            } else if (v == setting) {
                openSetting();
            } else if (v == cancelButton) {
                nowIsWarning = false;
                isCancel = true;
                timeCount.cancel();
                //恢复正常计步
                cancelButton.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                timeAnimation.clearAnimation();
                timeAnimation.setVisibility(View.GONE);
                notTime.setVisibility(View.VISIBLE);
            }
        }
    }

    private void skipWelcomeActivity() {
        isManualDisconnect = true;
        Intent newIntent = new Intent(MainActivity.this, WelcomeActivity.class);
        Bundle bundle = new Bundle(); //创建Bundle对象
        bundle.putBoolean(WelcomeActivity.M2W, false);     //装入数据
        newIntent.putExtras(bundle);
        startActivity(newIntent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * 发送字符串数据
     */
    private void sendData(String message) {
        byte[] value;
        try {
            value = message.getBytes("UTF-8");
            mService.writeRXCharacteristic(value);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /**
             * 连接成功
             */
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                connect.setText(R.string.disconnect);
                tv_bluetooth_name.setText(getString(R.string.bluetooth_name) + mDevice.getName());
                mState = UART_PROFILE_CONNECTED;
                tv_rssi.setText(getString(R.string.rssi) + rssi + "dBm");
                tv_battery.setText(getString(R.string.fullBattery));
            }
            /**
             * 连接断开
             */
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                if (isManualDisconnect) {
                    return;
                }
                connect.setText(R.string.connect);
                tv_bluetooth_name.setText(R.string.no_bt);
                tv_battery.setText(getString(R.string.battery));
                tv_rssi.setText(R.string.rssi_null);
                mService.connect(deviceAddress);
                mState = UART_PROFILE_DISCONNECTED;
            }
            /**
             * 获取数据
             */
            double stepLength;
            int w0 = Integer.parseInt(sharedPreferences.getString(SetActivity.SET_HEIGHT, "160"));
            if (w0 < 150) {
                stepLength = 0.4;
            } else if (w0 < 160) {
                stepLength = 0.5;
            } else {
                stepLength = 0.6;
            }
            int w1 = Integer.parseInt(sharedPreferences.getString(SetActivity.SET_WEIGHT, "50"));
            int w2 = 80;
            if (sharedPreferences.getString(Choose.GENDER, getString(R.string.male)).equals(getString(R.string.male))) {
                w2 = 100;
            }
            double calorie_value;
            int stepValue;
            DecimalFormat df = new DecimalFormat("######0.000");
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {
                String s_Value = intent.getStringExtra(UartService.EXTRA_DATA);
                if (TextUtils.isEmpty(s_Value)) {
                    return;
                }
                stepValue = Integer.parseInt(s_Value);
//              接受到CHAR4的计步数据
                step.setText(getString(R.string.step) + "\n" + stepValue);
                calorie_value = 3.330416666666667E-008D * w0 * w1 * w2 * stepValue;
                calorie.setText(getString(R.string.calorie) + "\n" + df.format(calorie_value));
                distance.setText(getString(R.string.distance) + "\n" + df.format(stepValue * stepLength));
//              接受到CHAR4的安全数据
                final String safeValue = intent.getStringExtra(UartService.SAFE_DATA);

                if (safeValue.equals("1") || safeValue.equals("2")) {
                    if (nowIsWarning) {
                        return;
                    }
                    isSafe = false;
                    if (safeValue.equals("2")) {
                        if (!sharedPreferences.getBoolean(WarningSetActivity.SAFE, false)) {
                            return;
                        }
                        isSafe = true;
                    }
                    if (!sharedPreferences.getBoolean(WarningSetActivity.FALL, false)) {
                        return;
                    }
                    //如果流量未开启，则开启数据流量
                    toggleMobileData(MainActivity.this, true);
                    //定位
                    getPosition();
                    nowIsWarning = true;
                    isCancel = false;
                    cancelButton.setText(getString(R.string.cancel));
                    cancelButton.setVisibility(View.VISIBLE);
                    time.setVisibility(View.VISIBLE);
                    timeAnimation.setVisibility(View.VISIBLE);
                    notTime.setVisibility(View.GONE);
                    myAnimation_Scale = AnimationUtils.loadAnimation(MainActivity.this, R.anim.my_scale_action);
                    timeAnimation.startAnimation(myAnimation_Scale);
                    timeCount = new TimeCount(10000, 1000);// 构造CountDownTimer对象
                    timeCount.start(); // 开始启动10秒倒计时
                }

                //获取RSSI
                final String rssiStatus = intent.getStringExtra(UartService.RSSI_STATUS);
                if (!TextUtils.isEmpty(rssiStatus)) {
                    if (rssiStatus.equals("0")) {
                        rssi = intent.getStringExtra(UartService.RSSI);
                        tv_rssi.setText(getString(R.string.rssi) + rssi + "dBm");
                    }
                }
                //写数据是否成功
                final String writeStatus = intent.getStringExtra(UartService.WRITE_STATUS);
                if (!TextUtils.isEmpty(writeStatus)) {
                    //写数据未成功
                    if (!writeStatus.equals("0")) {
                        //重新获取门锁的状态
                        mService.readCharacteristic(UartService.RX_SERVICE_UUID, UartService.RX_CHAR_UUID);
                    }
                }
            }
            /**
             * 获取电量
             */
            if (action.equals(UartService.EXTRAS_DEVICE_BATTERY)) {
                final String txValue = intent.getStringExtra(UartService.EXTRA_DATA);
                tv_battery.setText(getString(R.string.battery_value) + txValue + "%");
            }
            /**
             * 发现服务后 发起获取通知数据的请求
             */
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                mService.enableTXNotification();
                //获取电量
                mService.readCharacteristic(UartService.Battery_Service_UUID, UartService.Battery_Level_UUID);
                //获取门锁的状态
                mService.readCharacteristic(UartService.RX_SERVICE_UUID, UartService.RX_CHAR_UUID);
            }
            /**
             * 接受设备不支持UART的广播
             */
            if (action.equals(UartService.DEVICE_DOES_NOT_SUPPORT_UART)) {
            }
        }
    };

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发

            String key = WarningSetActivity.FALL;
            String text = getString(R.string.fallWarning);
            if (isSafe) {
                key = WarningSetActivity.SAFE;
                text = getString(R.string.safeWarning);
            }
            time.setText(text + getString(R.string.messageHasSend));
            cancelButton.setText(getString(R.string.sure));
            // 开始发送短信
            SmsManager manager = SmsManager.getDefault();
            ArrayList<String> texts = manager.divideMessage(sharedPreferences.getString(key + "Message", "")
                    + tv2);
            try {
                manager.sendMultipartTextMessage(sharedPreferences.getString(key + "Phone", "")
                        , null, texts, null, null);
            } catch (Exception e) {
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            mVibrator01 = (Vibrator) getApplication().getSystemService(
                    Service.VIBRATOR_SERVICE);
            mVibrator01.vibrate(new long[]{100, 10, 100, 1000}, -1);
            String key = getString(R.string.fallWarning);
            if (isSafe) {
                key = getString(R.string.safeWarning);
            }
            time.setText(key + getString(R.string.countTime) + millisUntilFinished / 1000 + getString(R.string.second));
        }
    }

    // 定位自己的位置，只定位一次
    public static String tv2 = "";

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location arg0) {
            int jingdu = (int) (arg0.getLatitude() * 1000000);
            int weidu = (int) (arg0.getLongitude() * 1000000);
            MKSearch search = new MKSearch();
            search.init(mapManager, new MyMKSearchListener());
            search.reverseGeocode(new GeoPoint(jingdu, weidu));
        }
    }

    // 供上一个函数调用
    class MyMKSearchListener implements MKSearchListener {

        @Override
        public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
            if (arg0 == null) {
                tv2 = getString(R.string.noPosition);
            } else {
                GeoPoint point = arg0.geoPt;
                tv2 = (getString(R.string.positionURL)
                        + (double) point.getLatitudeE6() / 1000000 + ","
                        + (double) point.getLongitudeE6() / 1000000
                        + getString(R.string.positionURL2) + arg0.strAddr);
            }
        }

        @Override
        public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
        }

        @Override
        public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
        }

        @Override
        public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
        }

        @Override
        public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
        }

        @Override
        public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
        }

        @Override
        public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
        }
    }

    // 常用事件监听，用来处理通常的网络错误，授权验证错误等
    class MyMKGeneralListener implements MKGeneralListener {

        @Override
        public void onGetNetworkState(int arg0) {
            if (arg0 == MKEvent.ERROR_NETWORK_CONNECT)
                CommonTools.showShortToast(MainActivity.this, getString(R.string.networkError));
        }

        @Override
        public void onGetPermissionState(int arg0) {

            if (arg0 == MKEvent.ERROR_PERMISSION_DENIED) {
                CommonTools.showShortToast(MainActivity.this, getString(R.string.KEYError));
            }
        }
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler.postDelayed(this, TIME);
                //每两秒读取一次Rssi
                mService.myReadRemoteRssi();
                if (nowIsWarning) {
                    if (isCancel) {
                        return;
                    }
                    timeAnimation.startAnimation(myAnimation_Scale);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void openSetting() {
        Intent newIntent = new Intent(MainActivity.this, SettingActivity.class);
        startActivityForResult(newIntent, REQUEST_CHANGE_NAME);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mState == UART_PROFILE_CONNECTED) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
                CommonTools.showShortToast(MainActivity.this, getString(R.string.Sign_out));
            } else {
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) { //监控/拦截菜单键
            openSetting();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 服务中间人
     */
//UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((UartService.LocalBinder) rawBinder).getService();
            if (!mService.initialize()) {
                finish();
            } else {
                //服务开启后 连接蓝牙
                mService.connect(deviceAddress);
            }
        }

        public void onServiceDisconnected(ComponentName classname) {
            mService = null;
        }
    };


    /**
     * 开启服务
     * 注册广播
     */
    private void service_init() {
        Intent bindIntent = new Intent(this, UartService.class);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    /**
     * 广播过滤器
     *
     * @return
     */
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(UartService.EXTRAS_DEVICE_BATTERY);
        return intentFilter;
    }

    /**
     * 当破坏Activity时调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(SMSBroadcastReceiver);
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(UARTStatusChangeReceiver);
        } catch (Exception ignore) {
        }
        unbindService(mServiceConnection);
        mService.stopSelf();
        mService = null;
        if (mapManager != null) {
            mapManager.destroy();
        }
    }

    /**
     * 移动网络开关
     */
    private void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = conMgr.getActiveNetworkInfo();
        if (networkinfo != null) {
            if (networkinfo.isConnected()) {
                return;
            }
        }
        Class<?> conMgrClass; // ConnectivityManager类
        Field iConMgrField; // ConnectivityManager类中的字段
        Object iConMgr; // IConnectivityManager类的引用
        Class<?> iConMgrClass; // IConnectivityManager类
        Method setMobileDataEnabledMethod; // setMobileDataEnabled方法
        try {
            // 取得ConnectivityManager类
            conMgrClass = Class.forName(conMgr.getClass().getName());
            // 取得ConnectivityManager类中的对象mService
            iConMgrField = conMgrClass.getDeclaredField("mService");
            // 设置mService可访问
            iConMgrField.setAccessible(true);
            // 取得mService的实例化类IConnectivityManager
            iConMgr = iConMgrField.get(conMgr);
            // 取得IConnectivityManager类
            iConMgrClass = Class.forName(iConMgr.getClass().getName());
            // 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
            setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            // 设置setMobileDataEnabled方法可访问
            setMobileDataEnabledMethod.setAccessible(true);
            // 调用setMobileDataEnabled方法
            setMobileDataEnabledMethod.invoke(iConMgr, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}

package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.ee5415.malu.easyexpress.myapplication2.Classes.MyDatabase;
import com.ee5415.malu.easyexpress.myapplication2.R;

/*to do list
*
* listView
* dateinput
* contentProvider
* 马路：登陆成功时，下载所有用户order，发送order成功时，才会将刚发送的order存入SQLite。
* 查看order时候，只显示SQLite中的order，因为前面两个步骤保证了和服务器的order记录同步。
* 至于offer的处理方式，暂时不用SQLite存储。纯粹网上读取。
*
* */
public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    private Toolbar mToolbar;
    MyLocationMapView mapView = null;// 显示地图的控件
    LocationData locationData = null;// location的数据保存对象
    LocationClient mLocationClient;// 定位的这样的类
    public MyLocationListener mListener = new MyLocationListener();// 监听的函数
    private MapController controller = null;// 一个Mapcontroller类

    private Button mSendButton,mTrackButton;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        DemoApplication application = (DemoApplication) this.getApplication();// 获得我的应用程序的实例
        if (application.mapManager == null) {// 要是管理员为空就 创建一个 管理对象
            application.mapManager = new BMapManager(getApplicationContext());
            application.mapManager
                    .init(new DemoApplication.MygeneralListener());// 注册一个listener对象
        }

        setContentView(R.layout.activity_main);
        MyDatabase.IP = PreferenceManager.getDefaultSharedPreferences(this).getString("prefServerIP","null");


        mapView = (MyLocationMapView) findViewById(R.id.bmapView);

        controller = mapView.getController();// 获得mapview的控制权
        mapView.getController().setZoom(17);// 设置缩放的级别
        mapView.getController().enableClick(true);// 能够获得点击事件
        mapView.showScaleControl(true);
        mapView.setBuiltInZoomControls(true);
        ///////////////////////

        mLocationClient = new LocationClient(getApplicationContext());
        locationData = new LocationData();
        mLocationClient.registerLocationListener(mListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd0911");
        option.setIsNeedAddress(true);
        option.setScanSpan(5000);
        option.setNeedDeviceDirect(true);
        mLocationClient.setLocOption(option);
        mLocationClient.start();


        //Send Button
        mSendButton = (Button) findViewById(R.id.send_button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SendStart.class);
                startActivity(i);
            }
        });
        //Track Button
        mTrackButton = (Button) findViewById(R.id.track_button);
        mTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,InquiryStart.class);
                startActivity(i);
            }
        });
        //Toolbar initialization.
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setTitle("             EasyExpress");
        mToolbar.setLogo(R.drawable.ic_launcher);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.background_toolbar));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.text_color_toolbar));
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.pref_settings){
                    Intent i = new Intent(MainActivity.this,UserSettingActivity.class);
                    startActivityForResult(i, 11);
                }else if(menuItem.getItemId() == R.id.user){
                    Intent i = new Intent(MainActivity.this,UserLogin.class);
                    startActivityForResult(i, 22);
                }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 11){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            MyDatabase.IP = pref.getString("prefServerIP", "null");
            Toast.makeText(this,"ServerIP is now "+ MyDatabase.IP,Toast.LENGTH_SHORT).show();

            if(pref.getBoolean(MyDatabase.mPrefUserInfoSave,false)){
                if(!MyDatabase.mLoginStatus.equalsIgnoreCase("false")){
                    pref.edit().putString(MyDatabase.mPrefUserPhone,MyDatabase.mCurrentUserPhone).commit();
                    pref.edit().putString(MyDatabase.mPrefUserPass,MyDatabase.mCurrentUserPass).commit();
                }
            }
        }else if(requestCode == 22){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            if (pref.getBoolean(MyDatabase.mPrefUserInfoSave,false)) {
                if (!MyDatabase.mLoginStatus.equalsIgnoreCase("false")) {
                    pref.edit().putString(MyDatabase.mPrefUserPhone, MyDatabase.mCurrentUserPhone).commit();
                    pref.edit().putString(MyDatabase.mPrefUserPass, MyDatabase.mCurrentUserPass).commit();
                }
            }
        }
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (location == null) {
                return;
            }
            mapView.refresh();// 刷新mapview
            Log.i("LocationOverlay", "接收到位置 移动到当前的位置");
            controller.animateTo(new GeoPoint(
                    (int) (location.getLatitude() * 1e6),
                    (int) (location.getLongitude() * 1e6)));
            MyDatabase.mAddress = location.getAddrStr();
            MyDatabase.mLat = String.valueOf(location.getLatitude());
            MyDatabase.mLong = String.valueOf(location.getLongitude());
//            Toast.makeText(MainActivity.this,MyDatabase.mAddress+" "+MyDatabase.mLong+" " + MyDatabase.mLat,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {

        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {

        mapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {

        if (mLocationClient != null)
            mLocationClient.stop();
        mapView.destroy();
        super.onDestroy();
    }
}
/*Toolbar methods
* void	inflateMenu(int resId)
Inflate a menu resource into this toolbar.

void	setTitle(int resId)
Set the title of this toolbar.
*/
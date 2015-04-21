package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
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
public class MainActivity extends MapActivity {
    /** Called when the activity is first created. */
    private Toolbar mToolbar;
    // 添加百度地图的相关控件
    private MapView mapView;
    private BMapManager bMapManager;// 加载地图的引擎
    // 百度地图的key
    private String keyString = "A270F85CD72A01E8519A9677A75FB4016ED9A5A3";
    // 在百度地图上添加一些控件，比如是放大或者缩小的控件
    private MapController mapController;

    private Button mSendButton,mTrackButton;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyDatabase.IP = PreferenceManager.getDefaultSharedPreferences(this).getString("prefServerIP","null");


        /////////////////////////////////////////////////
//        mapView = (MapView) this.findViewById(R.id.bmapView);
//        bMapManager = new BMapManager(MainActivity.this);
//        // 必须要加载key
//        bMapManager.init(keyString, new MKGeneralListener() {
//            @Override
//            public void onGetPermissionState(int arg0) {
//                // TODO Auto-generated method stub
//                if (arg0 == 300) {
//                    Toast.makeText(MainActivity.this, "输入的Key有错！请核实！！",Toast.LENGTH_SHORT).show();
//                }
//            }
//            @Override
//            public void onGetNetworkState(int arg0) {
//                // TODO Auto-generated method stub
//            }
//        });
//
//        this.initMapActivity(bMapManager);
//        mapView.setBuiltInZoomControls(true);// 表示可以设置缩放功能
//        mapController = mapView.getController();
//        // 需要定义一个经纬度：北京天安门
//        GeoPoint geoPoint = new GeoPoint((int) (22.339433 * 1E6),
//                (int) (114.183483 * 1E6));
//
//        mapController.setCenter(geoPoint);// 设置一个中心点
//        mapController.setZoom(12);// 设置缩放级别是12个级别
//////////////////////////////////////////////////////////////////////////////////////////////
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
        mToolbar.setTitle(" EasyExpress");
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (bMapManager != null) {
            bMapManager.destroy();
            bMapManager = null;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (bMapManager != null) {
            bMapManager.start();
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (bMapManager != null) {
            bMapManager.stop();
        }
    }

    @Override
    protected boolean isRouteDisplayed() {
        // TODO Auto-generated method stub
        return false;
    }
}
/*Toolbar methods
* void	inflateMenu(int resId)
Inflate a menu resource into this toolbar.

void	setTitle(int resId)
Set the title of this toolbar.
*/
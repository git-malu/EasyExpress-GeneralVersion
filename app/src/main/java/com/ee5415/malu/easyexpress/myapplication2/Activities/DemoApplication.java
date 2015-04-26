package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

//final类型的，也就是所有的子类都不能继承该方法
public class DemoApplication extends Application {

    BMapManager mapManager;// 一个map管理者对象
    public boolean isKeyright;// 是否key是对的
    private static DemoApplication mInstance = null;// 一个mapapplication的实例

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;// 把这个自身类的值传给了mInstance
        InitEngineManager(this);// 初始化

    }

    public void InitEngineManager(Context context) {
        if (mapManager == null) {// 要是没有管理员 就创建一个管理员
            mapManager = new BMapManager(context);
        }
        // ///////////////////////////////////////////////////////////
        // 初始化一个监听事件
        if (!mapManager.init(new MygeneralListener())) {
            Toast.makeText(
                    DemoApplication.getInstance().getApplicationContext(),
                    "初始化失败", Toast.LENGTH_LONG).show();
        }

    }

    public static DemoApplication getInstance() {
        return mInstance;// 返回当前的this
    }

    static class MygeneralListener implements MKGeneralListener {
        @Override
        public void onGetNetworkState(int iError) {
            // TODO Auto-generated method stub
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(
                        DemoApplication.getInstance().getApplicationContext(),
                        "不好意思网络有错", Toast.LENGTH_LONG).show();
            }

            if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(
                        DemoApplication.getInstance().getApplicationContext(),
                        "输入正确的检索条件！", Toast.LENGTH_LONG).show();

            }

        }

        @Override
        public void onGetPermissionState(int iError) {
            // TODO Auto-generated method stub
            // 非零值表示key验证未通过
            if (iError != 0) {
                Toast.makeText(
                        DemoApplication.getInstance().getApplicationContext(),
                        "请检查你的key是否正确", Toast.LENGTH_LONG).show();
                DemoApplication.getInstance().isKeyright = false;
            } else {
                Toast.makeText(
                        DemoApplication.getInstance().getApplicationContext(),
                        "key认证成功", Toast.LENGTH_LONG).show();
                DemoApplication.getInstance().isKeyright = true;
            }

        }
    }

}

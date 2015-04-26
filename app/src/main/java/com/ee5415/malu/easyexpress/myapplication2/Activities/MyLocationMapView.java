package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.content.Context;
import android.util.AttributeSet;

import com.baidu.mapapi.map.MapView;

//写这个MyLocationMapView继承mapview的作用是为了使用onTouchEvent 这样我就能在点击的时候 隐藏PopupOverlay
public class MyLocationMapView extends MapView {
    
    public MyLocationMapView(Context context) {
        super(context);
        // // TODO Auto-generated constructor stub
    }

    public MyLocationMapView(Context context, AttributeSet attrs) {

        super(context, attrs);
    }

    public MyLocationMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}

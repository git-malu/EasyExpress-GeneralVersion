package com.ee5415.malu.easyexpress.myapplication2.Classes;

import android.provider.BaseColumns;

/**
 * Created by malu on 4/2/15.
 * table user_info columns: _ID(primary key) user_name user_pass ,where _ID is the user phone number.
 *
 * table order_records colums: _ID(primary key) from to user_phone(foreign key) courier_phone expected_time descriptions status, 8 columns here.
 */
public class MyDatabase {
    public static final String DATABASE_NAME = "easy_express_database";
    public static String mCurrentUserName;
    public static String mCurrentUserPhone;
    public static String mCurrentUserPass;
    public static String IP = "144.214.103.219";
    public static String mLoginStatus = "false";//offline,true,false
    public static String mSendStatus = "false";

    public static final String mPrefUserInfoSave = "prefUserInfoSave";
    public static final String mPrefServerIP = "prefServerIP";
    public static final String mPrefUserPhone = "prefUserPhone";
    public static final String mPrefUserPass = "prefUserPass";


    public static abstract class UserInfo implements BaseColumns{
        //phone number is used as _ID of baseColumns
        public static final String TABLE_NAME = "user_info";
        public static final String USER_NAME = "user_name";
        public static final String USER_PASS = "user_pass";
    }

    public  static abstract class OrderRecords implements BaseColumns{//_ID contained !!!!!!!!!!!!!!!!!
        //phone number is used as foreign key in this table
        public static final String TABLE_NAME = "order_records";
        public static final String FROM = "_from";
        public static final String TO = "_to";
        public static final String USER_PHONE = "user_phone";
        public static final String COURIER_PHONE = "courier_phone";
        public static final String EXPECTED_TIME = "expected_time";
        public static final String DESCRIPTIONS = "descriptions";
        public static final String STATUS = "status";
    }

    public static abstract class Offers implements BaseColumns{
        //the primary key is a composition key.  order_id + courier_phone
        public static final String TABLE_NAME = "offer_records";
        //6 columns
        public static final String COURIER_PHONE = "courier_phone";
        public static final String PRICE = "price";
        public static final String PICK_TIME = "pick_time";
        public static final String PACKAGE_ARRIVAL_TIME = "package_arrival_time";
        public static final String COURIER_LOCATION ="courier_location";
    }
}

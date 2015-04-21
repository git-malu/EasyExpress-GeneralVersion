package com.ee5415.malu.easyexpress.myapplication2.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by malu on 4/2/15.
 * I GOT an idea, just two table. One for the users' info, the other for all the records from all the users,
 * no matter the orders checked or not checked. The key point is that I use the _ID, the phone number, as foreign key in the
 * records table.
 * table user_info columns: _ID(primary key) user_name user_pass ,where _ID is the user phone number.
 *
 * table order_records columns: _ID(primary key) _from _to user_phone(foreign key) courier_phone expected_time descriptions status, 8 columns here. status:wait, accepted only 2(we can't get "done" status! )
 *
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String mDatabaseName = MyDatabase.DATABASE_NAME;
    public static int mDataBaseVersion = 1;
    //create table tableName(_ID text primary key, user_name text, user_pass text);
    public static final String CREATE_TABLE_USER = "create table "+MyDatabase.UserInfo.TABLE_NAME+" ("
            +MyDatabase.UserInfo._ID+" text primary key, "
            +MyDatabase.UserInfo.USER_NAME+" text, "
            +MyDatabase.UserInfo.USER_PASS + " text);";
    //create table tableName(_ID text primary key, from text, to text, user_phone text, courier_phone text, expected_time text, descriptions text, status text,
    //foreign key (user_phone) references user_info(_ID));
    public static final String CREATE_TABLE_RECORDS = "create table "+ MyDatabase.OrderRecords.TABLE_NAME+"("
            + MyDatabase.OrderRecords._ID +" text primary key, "
            + MyDatabase.OrderRecords.FROM +" text, "
            + MyDatabase.OrderRecords.TO +" text, "
            + MyDatabase.OrderRecords.USER_PHONE + " text, "
            + MyDatabase.OrderRecords.COURIER_PHONE + " text, "
            + MyDatabase.OrderRecords.EXPECTED_TIME + " text, "
            + MyDatabase.OrderRecords.DESCRIPTIONS + " text, "
            + MyDatabase.OrderRecords.STATUS + " text, "
            + "foreign key"+" ("+ MyDatabase.OrderRecords.USER_PHONE+") "
            + "references " + MyDatabase.UserInfo.TABLE_NAME + "("+ MyDatabase.UserInfo._ID+"));";

    public static final String CREATE_TABLE_OFFERS = "create table " + MyDatabase.Offers.TABLE_NAME + "("
            + MyDatabase.Offers._ID + " text,"//order_ID
            + MyDatabase.Offers.COURIER_PHONE + " text, "
            + MyDatabase.Offers.PRICE +" text, "
            + MyDatabase.Offers.PICK_TIME + " text, "
            + MyDatabase.Offers.PACKAGE_ARRIVAL_TIME + " text, "
            + MyDatabase.Offers.COURIER_LOCATION + " text, "
            + "primary key ("+ MyDatabase.Offers._ID + ", " + MyDatabase.Offers.COURIER_PHONE + "));";

    public MySQLiteHelper(Context context) {
        super(context, mDatabaseName, null, mDataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_RECORDS);
        db.execSQL(CREATE_TABLE_OFFERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long registerUserInfo(SQLiteOpenHelper helper, String user_phone, String user_name, String user_pass){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDatabase.UserInfo._ID,user_phone);
        cv.put(MyDatabase.UserInfo.USER_NAME,user_name);
        cv.put(MyDatabase.UserInfo.USER_PASS,user_pass);
        return db.insert(MyDatabase.UserInfo.TABLE_NAME,null,cv);
    }

    public void queryUserInfo(SQLiteOpenHelper helper){

    }

    public long insertOrderRecord(SQLiteOpenHelper helper, String order_id, String from, String to, String user_phone, String courier_phone, String expected_time, String description, String status ){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDatabase.OrderRecords._ID,order_id);
        cv.put(MyDatabase.OrderRecords.FROM,from);
        cv.put(MyDatabase.OrderRecords.TO,to);
        cv.put(MyDatabase.OrderRecords.USER_PHONE,user_phone);
        cv.put(MyDatabase.OrderRecords.COURIER_PHONE,courier_phone);
        cv.put(MyDatabase.OrderRecords.EXPECTED_TIME,expected_time);
        cv.put(MyDatabase.OrderRecords.DESCRIPTIONS,description);
        cv.put(MyDatabase.OrderRecords.STATUS,status);
        long result = db.insert(MyDatabase.OrderRecords.TABLE_NAME,null,cv);
        db.close();
        return result;
    }

    public Cursor queryOrderRecords(SQLiteOpenHelper helper,String[] columns, String whereClause, String[] whereArgs){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cs;
        try {
            cs = db.query(MyDatabase.OrderRecords.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
            cs.moveToFirst();
        }catch (IllegalArgumentException ilEx){
            cs = null;
        }finally {
            db.close();
        }
        return cs;
    }
    public void updateOrderRecords(String courier_phone,String status,String whereClause,String[] whereArgs){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDatabase.OrderRecords.STATUS,status);
        cv.put(MyDatabase.OrderRecords.COURIER_PHONE,courier_phone);
        db.update(MyDatabase.OrderRecords.TABLE_NAME,cv,whereClause,whereArgs);
        db.close();
    }
    public long insertOfferRecord(SQLiteOpenHelper helper,String order_id,String courier_phone,String price, String pick_time,String arrival_time,String courier_location){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MyDatabase.Offers._ID,order_id);
        cv.put(MyDatabase.Offers.COURIER_PHONE,courier_phone);
        cv.put(MyDatabase.Offers.PRICE,price);
        cv.put(MyDatabase.Offers.PICK_TIME,pick_time);
        cv.put(MyDatabase.Offers.PACKAGE_ARRIVAL_TIME,arrival_time);
        cv.put(MyDatabase.Offers.COURIER_LOCATION,courier_location);
        long result = db.insert(MyDatabase.Offers.TABLE_NAME,null,cv);
        db.close();
        return result;
    }
    public Cursor queryOffers(SQLiteOpenHelper helper,String[] columns, String whereClause, String[] whereArgs){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cs;
        try {
            cs = db.query(MyDatabase.Offers.TABLE_NAME, columns, whereClause, whereArgs, null, null, null);
            cs.moveToFirst();
        }catch (IllegalArgumentException ilEx){
            cs = null;
        }finally {
            db.close();
        }
        return cs;
    }






}

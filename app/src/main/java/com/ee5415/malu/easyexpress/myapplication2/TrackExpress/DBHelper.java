package com.ee5415.malu.easyexpress.myapplication2.TrackExpress;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ee5415.malu.easyexpress.myapplication2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class DBHelper extends SQLiteOpenHelper {

	public final static String SQLNAME = "sql.db";
	public final static String DBCREATEINFOTABLE = "create table InfoTable(autokey integer primary key autoincrement,name text,id text,count int)";
	public final static String INFOTABLE = "InfoTable";
	public final static String DBCREATESAVETABLE = "create table SaveTable(autokey integer primary key autoincrement,code text,id text,name text,info text)";
	public final static String SAVETABLE = "SaveTable";
	private SQLiteDatabase mdatabase;
	private Context c;
	public DBHelper(Context context
    ) {
		super(context, SQLNAME, null, 1);
		// TODO Auto-generated constructor stub
		this.c = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(this.DBCREATEINFOTABLE);
		db.execSQL(this.DBCREATESAVETABLE);
	        if(this.isInfoTableEmpty(db))
	        {
	        	InputStream in = c.getResources().openRawResource(R.raw.ids);
	        	try {
	        		BufferedReader br = new BufferedReader(new InputStreamReader(in,"gbk"));
	        		String str = "";
	        		String tem = "";
	        		while((tem=br.readLine())!=null){

	        			str+=tem;
	        		}

	        		br.close();

	        		JSONObject myjson = new JSONObject(str);

	        		JSONArray jsonarr = myjson.getJSONArray("ids");
	        		for(int i = 0;i<jsonarr.length();i++){

	        			JSONObject njson = jsonarr.getJSONObject(i);
	        			String name1 =njson.getString("name");
	        			String id1 = njson.getString("id");
	        			Log.d("jsonstr", name1);
	        			ContentValues cv = new ContentValues();
	        			cv.put("name", name1);
	        			cv.put("id", id1);
	        			cv.put("count", 0);
	        			db.insert(INFOTABLE, null, cv);
	        		}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("UnsupportedEncodingException", "UnsupportedEncodingException");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("IOException", "IOException");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("JSONException", "JSONException");
			}
	        }
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

	public boolean detfromSaveTable(int autokey){
		this.mdatabase = this.getWritableDatabase();
		this.mdatabase.delete(this.SAVETABLE, "autokey=?", new String[]{Integer.toString(autokey)});
		return true;
	}

	public void insertSaveTable(String code,String id ,String name,String info){
		this.mdatabase = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("code", code);
		cv.put("id", id);
		cv.put("name", name);
		cv.put("info", info);
		this.mdatabase.insert(this.SAVETABLE, null, cv);
	}
	public void delSaveTable(String code,String id){
		this.mdatabase = this.getWritableDatabase();
		this.mdatabase.delete(this.SAVETABLE, "code = ? and id = ?", new String[]{code,id});
	}
	public void insertSaveTableIfnotExits(String code ,String id ,String name,String info){
		this.mdatabase = this.getWritableDatabase();
		Cursor c = this.mdatabase.query(this.SAVETABLE, null, "code=? and id = ?", new String[]{code,id}, null, null, null);
		if(c.getCount()==0){
			insertSaveTable(code, id, name,info);
		}
	}

	public void updateSaveTable(int autokey,String info){
		this.mdatabase = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("info", info);
		this.mdatabase.update(this.SAVETABLE, cv, "autokey=?", new String[]{Integer.toString(autokey)});
	}

	public Cursor querySaveTable(){
		this.mdatabase = this.getReadableDatabase();
		Cursor cur = this.mdatabase.query(SAVETABLE, null, null, null, null, null, "autokey desc");
		return cur;
	}

	public void increaseInfoTableCount(int autokey){
		this.mdatabase = this.getWritableDatabase();
		this.mdatabase.execSQL("Update "+this.INFOTABLE+" set count = count+1 where autokey="+autokey);
	}

	public void insertInfoTable(String name,String id,int count){
		this.mdatabase = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("name", name);
		cv.put("id", id);
		cv.put("count", count);
		this.mdatabase.insert(this.INFOTABLE, null, cv);
	}

	public Cursor queryInfoTable(){
		this.mdatabase = this.getReadableDatabase();
		Cursor cur = this.mdatabase.query(this.INFOTABLE, null, null, null, null, null, "count desc");
		return cur;
	}

	public boolean isInfoTableEmpty(){
		this.mdatabase = this.getReadableDatabase();
		Cursor cur = this.mdatabase.query(this.INFOTABLE, null, null, null, null, null, null);
		return (cur.getCount()==0);			
	}
	public boolean isInfoTableEmpty(SQLiteDatabase db){
		this.mdatabase = db;
		Cursor cur = this.mdatabase.query(this.INFOTABLE, null, null, null, null, null, null);
		return (cur.getCount()==0);
	}

}

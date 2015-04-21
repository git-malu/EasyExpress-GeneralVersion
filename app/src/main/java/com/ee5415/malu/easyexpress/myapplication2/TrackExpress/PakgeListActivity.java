package com.ee5415.malu.easyexpress.myapplication2.TrackExpress;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.ee5415.malu.easyexpress.myapplication2.R;

import java.util.ArrayList;

public class PakgeListActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {

	ListView lvpakge;
	ArrayList<String> listid = new ArrayList<String>();
	ArrayList<String> listcomp = new ArrayList<String>();
	ArrayList<String> listcode = new ArrayList<String>();
	ArrayList<String> listinfo = new ArrayList<String>();
	Button btnMadd;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pakge_list);

        
        btnMadd = (Button)findViewById(R.id.btnSPakge);
        btnMadd.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(PakgeListActivity.this, SearchMainPage.class);
				startActivity(it);
			}});
        
        lvpakge = (ListView)findViewById(R.id.lvpacklist);
        init();
        lvpakge.setOnItemClickListener(this);
        lvpakge.setOnItemLongClickListener(this);
        
    }
    public void init(){
        DBHelper dbh = new DBHelper(this);
        Cursor cur = dbh.querySaveTable();
        listid = new ArrayList<String>();
    	listcomp = new ArrayList<String>();
    	listcode = new ArrayList<String>();
    	listinfo = new ArrayList<String>();
        while(cur.moveToNext()){
        	listcode.add(cur.getString(1));
        	listid.add(cur.getString(2));
        	listcomp.add(cur.getString(3));
        	listinfo.add(cur.getString(4));
        }
        lvpakge.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,listcode));
    }
    public void onResume(){
    	init();
    	super.onResume();
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		Intent it = new Intent();
		it.setClass(this, PakgeInfoActivity.class);
		it.putExtra("id", listid.get(position));
		it.putExtra("code", listcode.get(position));
		it.putExtra("comp", listcomp.get(position));
		it.putExtra("info", listinfo.get(position));
		startActivity(it);
	}
	private int p ;
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v, int position,
			long id) {
		// TODO Auto-generated method stub
	    p = position;
		Dialog dialog =  new AlertDialog.Builder(this).setTitle(R.string.message_Title).setMessage(R.string.message_Content).setPositiveButton(R.string.message_yes, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				DBHelper dbh = new DBHelper(PakgeListActivity.this);
				dbh.delSaveTable(listcode.get(p), listid.get(p));
				listid.remove(p);
				listcode.remove(p);
				listcomp.remove(p);
				listinfo.remove(p);
				lvpakge.setAdapter(new ArrayAdapter(PakgeListActivity.this,android.R.layout.simple_list_item_1,listcode));
			}
		}).setNegativeButton(R.string.message_no, null).create();
		dialog.show();
		return true;
	}
}

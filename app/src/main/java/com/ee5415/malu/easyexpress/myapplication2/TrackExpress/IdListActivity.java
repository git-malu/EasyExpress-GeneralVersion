package com.ee5415.malu.easyexpress.myapplication2.TrackExpress;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ee5415.malu.easyexpress.myapplication2.R;

import java.util.LinkedList;

public class IdListActivity extends Activity {

	LinkedList<String> listinfo;

	LinkedList<String> listdata;

	LinkedList<Integer> listautokey;

	ListView lvkuaidi;
	DBHelper dbh ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_id_list);

        
        listinfo = new LinkedList<String>();
        listdata = new LinkedList<String>();
        listautokey = new LinkedList<Integer>();
        lvkuaidi = (ListView)findViewById(R.id.lvlist);

        dbh = new DBHelper(this);
        Cursor cur = dbh.queryInfoTable();
        while(cur.moveToNext()){
        int autokey = cur.getInt(0);
        String name = cur.getString(1);
        String id = cur.getString(2);
        listautokey.add(autokey);
        listinfo.add(name);
        listdata.add(id);
        }

        lvkuaidi.setAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_1,listinfo));

        lvkuaidi.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				

				Intent data = new Intent();
				

				data.putExtra("name", listinfo.get(position));
				data.putExtra("id", listdata.get(position));
				IdListActivity.this.dbh.increaseInfoTableCount(listautokey.get(position));
				setResult(0,data);

				IdListActivity.this.finish();
			}});
        
    }

    @Override
    public boolean onKeyDown(int keycode,KeyEvent event){
    	if(event.getKeyCode()== KeyEvent.KEYCODE_BACK){
    		Intent data = new Intent();
    		data.putExtra("name", "Company");
    		data.putExtra("id", "");
    		this.setResult(0, data);
    		this.finish();
    		return true;
    	}
    	else{
    		return super.onKeyDown(keycode, event);
    	}
    }
    

}

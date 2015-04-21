package com.ee5415.malu.easyexpress.myapplication2.TrackExpress;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ee5415.malu.easyexpress.myapplication2.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMainPage extends Activity {

	Button btncomp;
	public final static int REQUESTCODE = 0;
	Button btnSearch;
	EditText edtCode;
	EditText edtinfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_search_main_page);

        edtinfo = (EditText)findViewById(R.id.edtinfo);

        edtCode = (EditText)findViewById(R.id.edtCode);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String id = (String)btncomp.getTag();
				String code = edtCode.getText().toString();
				if(TextUtils.isEmpty(id)){
					Toast.makeText(SearchMainPage.this, R.string.noComp, Toast.LENGTH_SHORT).show();
					return ;
				}
				if(TextUtils.isEmpty(code)){
					Toast.makeText(SearchMainPage.this, R.string.noCode, Toast.LENGTH_SHORT).show();
					return ;
				}
				Pattern p = Pattern.compile("[\\da-zA-z]+?");
				Matcher m = p.matcher(code);
				if(!m.matches()){
					Toast.makeText(SearchMainPage.this, "Enter the right code!", Toast.LENGTH_SHORT).show();
					return ;
				}
				
				Intent it = new Intent();
				it.setClass(SearchMainPage.this, PakgeInfoActivity.class);
				it.putExtra("id", id);
				it.putExtra("code", code);
				it.putExtra("comp", btncomp.getText().toString());
				it.putExtra("info", edtinfo.getText().toString());
				startActivity(it);
			}});
        
        btncomp = (Button)findViewById(R.id.btnkuaidi);
        btncomp.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.setClass(SearchMainPage.this, IdListActivity.class);
				startActivityForResult(it,REQUESTCODE);
			}});
    }


    protected void onActivityResult(int requestCode,int resultCode,Intent data){
    	if(requestCode==REQUESTCODE){
    		String name = data.getStringExtra("name");
    		String id = data.getStringExtra("id");
    		
    		this.btncomp.setText(name);
    		this.btncomp.setTag(id);
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
}

package com.ee5415.malu.easyexpress.myapplication2.TrackExpress;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5415.malu.easyexpress.myapplication2.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PakgeInfoActivity extends Activity {
	static final int FINISH = 0;
	static final int ERROR = -1;
	static final int WAIT = 1;
	String code ="";
	String id = "";
	String info = "";
	TextView tvcode;
	TextView tvid;
	TextView tvinfo;
	String comp = "";
	Handler mHandler;
	Thread mThread;
	ProgressDialog waitdialog;
	Button btnSave;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_pakge_info);

        Intent data = this.getIntent();
        code = data.getStringExtra("code");
        id = data.getStringExtra("id");
        info = data.getStringExtra("info");
        comp = data.getStringExtra("comp");

        tvcode = (TextView)findViewById(R.id.tvnum);
        tvcode.setText(R.string.title_Code+code);
        tvid = (TextView)findViewById(R.id.tvcomp);
        tvid.setText(R.string.title_Comp+comp);
        tvinfo = (TextView)findViewById(R.id.tvinfo);
        tvinfo.setText(R.string.title_Info);
        waitdialog = new ProgressDialog(this);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DBHelper dbh = new DBHelper(PakgeInfoActivity.this);
				dbh.insertSaveTableIfnotExits(code, id,comp,info);
				Toast.makeText(PakgeInfoActivity.this, R.string.button_Save, Toast.LENGTH_SHORT).show();
			}});
        this.mHandler = new Handler(){
        	@Override
        	public void handleMessage(Message msg){
        		switch(msg.what){
        		case WAIT:
        			waitdialog.show();
        			break;
        		case FINISH:
        			waitdialog.cancel();
        			tvinfo.setText((String)msg.obj);
        			break;
        		case ERROR:
        			waitdialog.cancel();
        			Toast.makeText(PakgeInfoActivity.this, "NO internet connect", Toast.LENGTH_SHORT).show();
        			break;
        		}
        	}
        };
        this.mThread = new Thread(){
        	@Override
        	public void run(){
        		int rand = (int) Math.random()*1000+1000;
        		String strurl = "http://wap.kuaidi100.com/q.jsp?rand="+rand+"&id="+id+"&postid="+code+"&fromWeb=null";
        		try {
        			Message msg2 = new Message();
        			msg2.what = WAIT;
        			mHandler.sendMessage(msg2);
					String str=UrltoHtmlString(strurl);
					
					Document doc = Jsoup.parse(str);
					Elements elems = doc.body().getElementsByTag("p");
					Object[] bojs = elems.toArray();
					String tstr = "";
					Pattern p = Pattern.compile("<.+?>|\\&gt;|\\&middot;", Pattern.DOTALL);
					
					for(int i = 0;i<bojs.length;i++){
						if(i == bojs.length-1){
							break;
						}
						String tem = bojs[i].toString();
						Matcher m = p.matcher(tem);
						
						tstr+=m.replaceAll("")+"\n";
						
					}
					
					Message msg = new Message();
					msg.what = FINISH;
					msg.obj = (Object)tstr;
					mHandler.sendMessage(msg);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = new Message();
					msg.what = ERROR;
					mHandler.sendMessage(msg);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = new Message();
					msg.what = ERROR;
					mHandler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Message msg = new Message();
					msg.what = ERROR;
					mHandler.sendMessage(msg);
				}
        	}
        };
        this.mThread.start();
    }



    public String UrltoHtmlString(String strurl) throws MalformedURLException,IOException {
    	String out ="";
    	URL url = new URL(strurl);
    	InputStream is = url.openStream();
    	BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
        
    	String tem = "";
    	while((tem=br.readLine())!=null){
    		out+=tem;
    	}
    	return out;
    }
}

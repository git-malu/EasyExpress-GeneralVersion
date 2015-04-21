package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ee5415.malu.easyexpress.myapplication2.Classes.ThreadRegister;
import com.ee5415.malu.easyexpress.myapplication2.R;

public class UserRegister extends ActionBarActivity {
    private Button mRegister;
    private EditText mUserName,mUserPhone,mUserPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        //wire up!
        mRegister = (Button) findViewById(R.id.register_button);

        mUserName = (EditText) findViewById(R.id.user_name);
        mUserPass = (EditText) findViewById(R.id.user_pass);
        mUserPhone = (EditText) findViewById(R.id.user_phone);

        //set listeners
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadRegister mThreadRegister = new ThreadRegister(UserRegister.this,mUserPhone.getText().toString(),mUserName.getText().toString(),mUserPass.getText().toString());
                mThreadRegister.start();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

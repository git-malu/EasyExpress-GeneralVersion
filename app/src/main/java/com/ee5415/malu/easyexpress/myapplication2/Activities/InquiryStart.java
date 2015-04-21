package com.ee5415.malu.easyexpress.myapplication2.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ee5415.malu.easyexpress.myapplication2.Fragments.MyOrderListViewFragment;
import com.ee5415.malu.easyexpress.myapplication2.R;
import com.ee5415.malu.easyexpress.myapplication2.TrackExpress.PakgeListActivity;

//import android.support.v7.widget.RecyclerView;


public class InquiryStart extends ActionBarActivity {
    private Toolbar mToolbar;
//    private RecyclerView mRecycler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_start);

        //set up the toolbar
        mToolbar = (Toolbar) findViewById(R.id.tool_bar);
        mToolbar.setBackgroundColor(getResources().getColor(R.color.primary_blue));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //

        //take care of fragment
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null){
            fragment = new MyOrderListViewFragment();
            fm.beginTransaction().add(R.id.fragment_container,fragment).commit();
        }


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
////        if(requestCode == 0 && resultCode == RESULT_OK){
//            String result_order_id = data.getStringExtra("order_id");
//        String result_courier_phone = data.getStringExtra("courier_phone");
//            Toast.makeText(this,"test", Toast.LENGTH_SHORT).show();
////        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inquiry_start, menu);
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
        if (id == R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        if (id == R.id.search_express){
            Intent intent = new Intent(this, PakgeListActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

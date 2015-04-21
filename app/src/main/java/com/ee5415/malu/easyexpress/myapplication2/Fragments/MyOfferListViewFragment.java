package com.ee5415.malu.easyexpress.myapplication2.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5415.malu.easyexpress.myapplication2.Classes.MyDatabase;
import com.ee5415.malu.easyexpress.myapplication2.Classes.MySQLiteHelper;
import com.ee5415.malu.easyexpress.myapplication2.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class MyOfferListViewFragment extends ListFragment {
    Context mCtx;
    Cursor cs;

    private static final int PORT = 12345;
    private static Socket link = null;
    private static PrintWriter out;
    private static Scanner in;
    public String mBuffer = "null";
    public String[] mBufferString;
    public String mOrderID;

    public MyOfferListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getActivity();
        Intent intent = getActivity().getIntent();
        mOrderID = intent.getStringExtra("order_id");
        //have a test first
//        Toast.makeText(getActivity(),mOrderID,Toast.LENGTH_SHORT).show();
        //start the thread here !
        new ThreadInquiryOffer().start();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(cs != null) {
            cs.close();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
        TextView tv =  (TextView)v.findViewById(R.id.order_id_textView);
        Toast.makeText(getActivity(),tv.getText().toString(), Toast.LENGTH_SHORT).show();

    }


    public Cursor queryDataBase(){
        MySQLiteHelper helper = new MySQLiteHelper(mCtx);
        String[] columns = {
                MyDatabase.Offers._ID,//0
                MyDatabase.Offers.COURIER_PHONE,//1
                MyDatabase.Offers.PRICE,//2
                MyDatabase.Offers.PICK_TIME,//3
                MyDatabase.Offers.PACKAGE_ARRIVAL_TIME,//4
        };
        String whereClause = MyDatabase.Offers._ID+" like ?";
        String[] whereArgs = {mOrderID};
        Cursor cs = helper.queryOffers(helper, columns, whereClause, whereArgs);
        return cs;
    }


    public class MyCursorAdapter extends CursorAdapter{

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return layoutInflater.inflate(R.layout.list_item_offer,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //wire up!
            TextView order_id = (TextView) view.findViewById(R.id.order_id_textView);
            final TextView courier_phone = (TextView) view.findViewById(R.id.courier_phone);
            TextView price = (TextView) view.findViewById(R.id.price);
            TextView pick_time = (TextView) view.findViewById(R.id.pick_time);
            TextView arrival = (TextView) view.findViewById(R.id.arrival_time);
            Button select = (Button) view.findViewById(R.id.select);
            Button call = (Button) view.findViewById(R.id.call);

            //fill in recycle view
            order_id.setText(cs.getString(0));
            courier_phone.setText(cs.getString(1));
            price.setText(cs.getString(2));
            pick_time.setText(cs.getString(3));
            arrival.setText(cs.getString(4));
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("order_id",cs.getString(0));
                    intent.putExtra("courier_phone",courier_phone.getText().toString());
                    getActivity().setResult(Activity.RESULT_OK,intent);
                }
            });

            //set listeners
            String pass = courier_phone.getText().toString();
            call.setOnClickListener(new View.OnClickListener() {
                String mString;
                private View.OnClickListener init(String str){
                    mString = str;
                    return this;
                }
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+mString));
                    startActivity(intent);
                }
            }.init(pass));
        }
    }
    public class ThreadInquiryOffer extends Thread{
        public ThreadInquiryOffer() {
        }

        @Override
        public void run() {
            try {
                link = new Socket(MyDatabase.IP, PORT);
                in = new Scanner(link.getInputStream());
                out = new PrintWriter(link.getOutputStream(), true);
                out.println("inquiry_offer_order_id:"+mOrderID);

                if(MyDatabase.mLoginStatus.equalsIgnoreCase("true")){
                    mBuffer = in.nextLine();
                    if (!mBuffer.equalsIgnoreCase("null")){
                        //split
                        mBufferString = mBuffer.split(":");
                        final MySQLiteHelper helper = new MySQLiteHelper(getActivity());
                        for(int i = 0; i<mBufferString.length/5; i++) {
                            helper.insertOfferRecord(helper,mBufferString[0+i*5],mBufferString[1+i*5],mBufferString[2+i*5],mBufferString[3+i*5],mBufferString[4+i*5],"null");//the null is courier position
                        }
                        //about UI
                        //set Adapter !!
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cs = queryDataBase();
                                setListAdapter(new MyCursorAdapter(mCtx,cs));
                            }
                        });
                    }
                }else {
                    //if offline just display
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            cs = queryDataBase();
                            setListAdapter(new MyCursorAdapter(mCtx,cs));
                        }
                    });
                }

            } catch (UnknownHostException uhEx) {
                System.out.println("not host find");
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            } finally {
//                cs.close();// close the Cursor !!!!!!!!!!!!
                try {
                    if (link != null) {
                        System.out.println("Closing down connection...");
                        link.close();
                    }
                } catch (IOException ioEx) {
                    ioEx.printStackTrace();
                }
            }
        }
    }
}

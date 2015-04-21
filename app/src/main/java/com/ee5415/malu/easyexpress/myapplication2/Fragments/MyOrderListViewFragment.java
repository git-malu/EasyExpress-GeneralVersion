package com.ee5415.malu.easyexpress.myapplication2.Fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ee5415.malu.easyexpress.myapplication2.Activities.OfferList;
import com.ee5415.malu.easyexpress.myapplication2.Classes.MyDatabase;
import com.ee5415.malu.easyexpress.myapplication2.Classes.MySQLiteHelper;
import com.ee5415.malu.easyexpress.myapplication2.R;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyOrderListViewFragment extends ListFragment {
    Context mCtx;
    Cursor cs;

    private static final int PORT = 12345;
    private static Socket link = null;
    private static PrintWriter out;
    private static Scanner in;
    public String mBuffer = "null";


    public MyOrderListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getActivity();
        cs = queryDataBase();
        setListAdapter(new MyCursorAdapter(mCtx,cs));

        /*getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cs != null) {
            cs.close();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
        TextView tv =  (TextView)v.findViewById(R.id.order_id_textView);
//        Toast.makeText(getActivity(),tv.getText().toString(), Toast.LENGTH_SHORT).show();

//        new ThreadInquiryOffer().start();
        Intent intent = new Intent(getActivity(), OfferList.class);
        intent.putExtra("order_id",tv.getText().toString());
        startActivityForResult(intent, 0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            //offer selected.
            //update the order status to accepted !!
            String result_order_id = data.getStringExtra("order_id");
            String result_courier_phone = data.getStringExtra("courier_phone");
            Toast.makeText(getActivity(), result_order_id +"  " + result_courier_phone + "I'm back!", Toast.LENGTH_LONG).show();//usable! tested.
            MySQLiteHelper helper = new MySQLiteHelper(getActivity());
            String whereClause = MyDatabase.OrderRecords._ID + " like ?";
            String[] whereArgs = {result_order_id};
            helper.updateOrderRecords(result_courier_phone,"accepted",whereClause,whereArgs);
            try{
                cs.close();
            }catch (Exception ex){
                ;
            }
            cs = queryDataBase();
            setListAdapter(new MyCursorAdapter(mCtx,cs));
            new ThreadUpdateStatus(result_order_id,result_courier_phone).start();
        }
    }

    //    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        getListView().set;
//
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv =  (TextView)view.findViewById(R.id.from_to_textView);
//                Toast.makeText(getActivity(),"just test",Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    public Cursor queryDataBase(){
        MySQLiteHelper helper = new MySQLiteHelper(mCtx);
        String[] columns = {
                MyDatabase.OrderRecords._ID,//0
                MyDatabase.OrderRecords.FROM,//1
                MyDatabase.OrderRecords.TO,//2
                MyDatabase.OrderRecords.COURIER_PHONE,//3
                MyDatabase.OrderRecords.EXPECTED_TIME,//4
                MyDatabase.OrderRecords.DESCRIPTIONS,//5
                MyDatabase.OrderRecords.STATUS//6
        };
        String whereClause = MyDatabase.OrderRecords.USER_PHONE+" like ?";
        //
        if(!MyDatabase.mLoginStatus.equalsIgnoreCase("false")) {
            String[] whereArgs = {MyDatabase.mCurrentUserPhone};
            Cursor cs = helper.queryOrderRecords(helper, columns, whereClause, whereArgs);
            return cs;
        }
        return null;
        //

    }


    public class MyCursorAdapter extends CursorAdapter{

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c, false);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return layoutInflater.inflate(R.layout.list_item,parent,false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            //wire up!
            TextView order_id = (TextView) view.findViewById(R.id.order_id_textView);
            TextView from_to_textView = (TextView) view.findViewById(R.id.from_to_textView);
            TextView phone_textView = (TextView) view.findViewById(R.id.phone_textView);
            TextView time_textView = (TextView) view.findViewById(R.id.time_textView);
            TextView description_textView = (TextView) view.findViewById(R.id.description_textView);
            CheckBox status = (CheckBox) view.findViewById(R.id.status_checkBox);

            //fill in recycle view
            order_id.setText(cs.getString(0));
            from_to_textView.setText("From " + cs.getString(1) + " to " + cs.getString(2));
            phone_textView.setText(cs.getString(3));
            time_textView.setText(cs.getString(4));
            description_textView.setText(cs.getString(5));
            if(cs.getString(6).equalsIgnoreCase("wait")){
                status.setChecked(false);
            }else if (cs.getString(6).equalsIgnoreCase("accepted")){
                status.setChecked(true);
            }
        }
    }

    public class ThreadUpdateStatus extends Thread{
        String result_order_id;
        String result_courier_phone;
        public ThreadUpdateStatus(String order_id,String courier_phone) {
            result_order_id = order_id;
            result_courier_phone = courier_phone;
        }

        @Override
        public void run() {
            try {
                link = new Socket(MyDatabase.IP, PORT);
                in = new Scanner(link.getInputStream());
                out = new PrintWriter(link.getOutputStream(), true);
                out.println("update_order_status:" + result_order_id);

//                if(MyDatabase.mLoginStatus.equalsIgnoreCase("true")){
//                    mBuffer = in.nextLine();
//                    if (!mBuffer.equalsIgnoreCase("null")){
//                        //split
//                        mBufferString = mBuffer.split(":");
//                        final MySQLiteHelper helper = new MySQLiteHelper(getActivity());
//                        for(int i = 0; i<mBufferString.length/5; i++) {
//                            helper.insertOfferRecord(helper,mBufferString[0+i*5],mBufferString[1+i*5],mBufferString[2+i*5],mBufferString[3+i*5],mBufferString[4+i*5],"null");//the null is courier position
//                        }
//                        //about UI
//                        //set Adapter !!
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                cs = queryDataBase();
//                                setListAdapter(new MyCursorAdapter(mCtx,cs));
//                            }
//                        });
//                    }
//                }else {
//                    //if offline just display
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            cs = queryDataBase();
//                            setListAdapter(new MyCursorAdapter(mCtx,cs));
//                        }
//                    });
//                }

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

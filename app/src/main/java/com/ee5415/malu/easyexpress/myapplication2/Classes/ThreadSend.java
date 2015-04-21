package com.ee5415.malu.easyexpress.myapplication2.Classes;

import android.widget.Toast;

import com.ee5415.malu.easyexpress.myapplication2.Activities.SendStart;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Lu on 3/29/2015.
 * use the constructor as input.
 * use the mBuffer as output.
 */
public class ThreadSend extends Thread {
    private static final int PORT = 12345;
    private static Socket link = null;
    private static PrintWriter out;
    private static Scanner in;
    public String mBuffer = "null";
    public String mStringSend;
    SendStart mActivity;



    public ThreadSend(SendStart activity,String str) {
        mActivity = activity;
        mStringSend = str;
    }

    @Override
    public void run() {
        try {
            link = new Socket(MyDatabase.IP, PORT);
            in = new Scanner(link.getInputStream());
            out = new PrintWriter(link.getOutputStream(), true);
            out.println(mStringSend);
            mBuffer = in.nextLine();

        } catch (UnknownHostException uhEx) {
            System.out.println("not host find");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } finally {
            try {
                if (link != null) {
                    System.out.println("Closing down connection...");
                    link.close();
                }
            } catch (IOException ioEx) {
                ioEx.printStackTrace();
            }
        }
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mBuffer.equalsIgnoreCase("true")){
                    MyDatabase.mSendStatus = "true";
                    Toast.makeText(mActivity,"FeedBack: Send Order successfully.",Toast.LENGTH_SHORT).show();
                    //let's insert into SQLite.
                    MySQLiteHelper helper = new MySQLiteHelper(mActivity);
                    //only 8 columns saved, position not saved, but sent.
                    helper.insertOrderRecord(helper,
                            mActivity.mOrderID,//order ID inserted !! made of user_phone + current date + current time as above
                            mActivity.mFrom.getText().toString(),
                            mActivity.mTo.getText().toString(),
                            MyDatabase.mCurrentUserPhone,
                            null,//courier phone set to null
                            mActivity.mExTime.getText().toString() + " " + mActivity.mExDate.getText().toString(),
                            mActivity.mDes.getText().toString(),
                            "wait");//order status set to "wait"
                    Toast.makeText(mActivity, "Order Save Successfully.", Toast.LENGTH_SHORT).show();
                    MyDatabase.mSendStatus = "false";
                    //just for test, remember to delete it.
//                mDes.setText(order_id);
                }
            }
        });
    }
}

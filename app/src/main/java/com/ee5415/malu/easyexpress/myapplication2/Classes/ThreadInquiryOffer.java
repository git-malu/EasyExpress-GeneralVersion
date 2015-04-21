package com.ee5415.malu.easyexpress.myapplication2.Classes;

import com.ee5415.malu.easyexpress.myapplication2.Activities.OfferList;

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
public class ThreadInquiryOffer extends Thread {
    private static final int PORT = 12345;
    private static Socket link = null;
    private static PrintWriter out;
    private static Scanner in;
    public String mBuffer = "null";
    public String[] mBufferString;
    public String mOrderID;
    OfferList mActivity;



    public ThreadInquiryOffer(OfferList activity, String order_id) {
        mActivity = activity;
        mOrderID = order_id;
    }

    @Override
    public void run() {
        try {
            link = new Socket(MyDatabase.IP, PORT);
            in = new Scanner(link.getInputStream());
            out = new PrintWriter(link.getOutputStream(), true);
            out.println("inquiry_offer_order_id:"+mOrderID);
            mBuffer = in.nextLine();
            if (!mBuffer.equalsIgnoreCase("null")){
                mBufferString = mBuffer.split(":");
                MySQLiteHelper helper = new MySQLiteHelper(mActivity);
                for(int i = 0; i<mBufferString.length/5; i++) {
                    helper.insertOfferRecord(helper,mBufferString[0+i*5],mBufferString[1+i*5],mBufferString[2+i*5],mBufferString[3+i*5],mBufferString[4+i*5],"null");//the null is courier position
                }
            }
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
    }
}

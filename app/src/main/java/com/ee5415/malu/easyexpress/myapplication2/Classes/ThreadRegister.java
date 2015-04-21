package com.ee5415.malu.easyexpress.myapplication2.Classes;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Created by Lu on 4/8/2015.
 */
public class ThreadRegister extends Thread {

    private static final int PORT = 12345;
    private static Socket link = null;
    private static Scanner in;
    private static PrintWriter out;
    public String mBuffer = "null";

    String mUserPhone,mUserName,mUserPass;

    Activity mActivity;
    public ThreadRegister(Activity activity,String user_phone,String user_name, String user_pass) {
        mActivity = activity;
        mUserPhone = user_phone;
        mUserName = user_name;
        mUserPass = user_pass;
    }

    @Override
    public void run() {
        try {
            link = new Socket(MyDatabase.IP, PORT);
            in = new Scanner(link.getInputStream());
            out = new PrintWriter(link.getOutputStream(), true);
            out.println("register:" + mUserPhone + ":" +mUserName+ ":" + mUserPass);
            mBuffer = in.nextLine();
        } catch (UnknownHostException uhEx) {
            System.out.println("not host find");
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        } catch (NoSuchElementException noEx){
            ;
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
                //if register successfully on the net, register offline too
                if(mBuffer.equalsIgnoreCase("true")){
                    //
                    MySQLiteHelper helper = new MySQLiteHelper(mActivity);
                    long result = helper.registerUserInfo(helper,mUserPhone,mUserName,mUserPass);
                    //
                    Toast.makeText(mActivity,"Registration success.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("user_name",mUserName);
                    intent.putExtra("user_pass",mUserPass);
                    intent.putExtra("user_phone",mUserPhone);
                    mActivity.setResult(mActivity.RESULT_OK,intent);
                    mActivity.finish();
                }else{
                    Toast.makeText(mActivity,"Registration failed.",Toast.LENGTH_SHORT).show();
                    Log.d("ThreadRegister",mBuffer);
                }
            }
        });
    }

}

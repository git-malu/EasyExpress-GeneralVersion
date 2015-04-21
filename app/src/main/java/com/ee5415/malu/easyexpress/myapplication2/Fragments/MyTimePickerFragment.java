package com.ee5415.malu.easyexpress.myapplication2.Fragments;


import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.TimePicker;

import com.ee5415.malu.easyexpress.myapplication2.R;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyTimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


    public MyTimePickerFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }
//    DateFormat.is24HourFormat(getActivity())

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        EditText edit_time = (EditText)getActivity().findViewById(R.id.edit_time);
        if(hourOfDay<10){
            if (minute<10){
                edit_time.setText("0"+String.valueOf(hourOfDay)+":"+"0"+String.valueOf(minute));
            }else{
                edit_time.setText("0"+String.valueOf(hourOfDay)+":"+String.valueOf(minute));
            }
        }else if(minute<10){
            edit_time.setText(String.valueOf(hourOfDay)+":"+"0"+String.valueOf(minute));
        }else{
            edit_time.setText(String.valueOf(hourOfDay)+":"+String.valueOf(minute));
        }
    }


}

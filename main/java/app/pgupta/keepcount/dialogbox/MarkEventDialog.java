package app.pgupta.keepcount.dialogbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.adapter.AllEventsAdapter;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.model.EventMaster;
import app.pgupta.keepcount.util.ThemeUtil;

/**
 * Created by admin on 5/8/2016.
 */
public class MarkEventDialog extends DialogFragment {

    public EventMaster master;
    public AllEventsAdapter.EventMarkedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        final Calendar currDay = Calendar.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v  = inflater.inflate(R.layout.dialog_add_master_event, null);
        TextView tvUnit = (TextView)v.findViewById(R.id.tvUnit);
        tvUnit.setText(master.getUnit());
        TextView tvTitle = (TextView)v.findViewById(R.id.tvEventTitle);
        tvTitle.setText(master.getTitle());
        LinearLayout llHeader = (LinearLayout)v.findViewById(R.id.llMarkDialogHeader);
        llHeader.setBackgroundResource(ThemeUtil.theme_background_resource);

        final NumberPicker day = (NumberPicker)v.findViewById(R.id.npDay);
        day.setMinValue(1);
        day.setMaxValue(currDay.get(Calendar.DAY_OF_MONTH));
       // day.setWrapSelectorWheel(false);
        day.setValue(day.getMaxValue());

        final NumberPicker hour = (NumberPicker)v.findViewById(R.id.npHour);
        hour.setMinValue(0);
        hour.setMaxValue(23);
       // hour.setWrapSelectorWheel(false);
        hour.setValue(currDay.get(Calendar.HOUR_OF_DAY));

        final NumberPicker min = (NumberPicker)v.findViewById(R.id.npMinute);
        min.setMinValue(0);
        min.setMaxValue(59);
       // min.setWrapSelectorWheel(false);
        min.setValue(currDay.get(Calendar.MINUTE));

        builder.setView(v);

        builder.setPositiveButton("MARK IT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etDesc = (EditText)v.findViewById(R.id.etEventDesc);
                EditText etVal = (EditText)v.findViewById(R.id.etValue);

                Event event = new Event();
                String desc = etDesc.getText().toString();
                if(desc == null || "".equals(desc)){
                    event.setDescription("No description was added");
                }else {
                    event.setDescription(desc);
                }
                try {
                    event.setValue(Float.valueOf(etVal.getText().toString()));
                }catch (Exception e){
                    Log.e("",e.getMessage());
                }
                event.setEventID(master.getId());
                event.setEventCategory(master.getEventCategory());
                event.setTitle(master.getTitle());
                currDay.set(currDay.get(Calendar.YEAR),currDay.get(Calendar.MONTH),day.getValue(),hour.getValue(),min.getValue());
                event.setTimestamp(String.valueOf(currDay.getTimeInMillis()));//TimeUtil.getCurrentTimeinMillis()));
                event.setUnit(master.getUnit());
                event.setGain_or_loss(master.getGain_or_loss());
                markEventToday(event);
                listener.onEventMarked(event);
                Toast.makeText(getContext(),"Marked successfully", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return;
            }
        });

        return builder.create();
    }

    private void markEventToday(Event event){

        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(getContext());
        handler.openConnection();
        long id = handler.createDailyEvent(event);
        handler.closeConnection();
        event.setRecordID((int)id);
    }
}

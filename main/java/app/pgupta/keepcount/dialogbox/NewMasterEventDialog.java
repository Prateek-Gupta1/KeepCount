package app.pgupta.keepcount.dialogbox;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.ThemeUtil;

/**
 * Created by admin on 5/7/2016.
 */
public class NewMasterEventDialog extends DialogFragment {


    public interface NewMasterEventDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, View view);
        public void onDialogNegativeClick(DialogFragment dialog, View view);
    }

    public NewMasterEventDialogListener mListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //return super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v  = inflater.inflate(R.layout.dialog_create_new_event,null);
        builder.setView(v);

        final EditText etTitle = (EditText) v.findViewById(R.id.etEventTitle);
        Spinner spEventCat = (Spinner) v.findViewById(R.id.spEventCategory);
        LinearLayout llHeader = (LinearLayout)v.findViewById(R.id.llCreateNewDialogHeader);
        llHeader.setBackgroundResource(ThemeUtil.theme_background_resource);
        String[] eventCategories = getEventCategories();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,eventCategories);
        spEventCat.setAdapter(adapter);

        builder.setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = etTitle.getText().toString();
                if(title==null || "".equals(title)){
                    Toast.makeText(getContext(),"Please write a title for this event", Toast.LENGTH_LONG).show();
                }else {
                    mListener.onDialogPositiveClick(NewMasterEventDialog.this, v);
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDialogNegativeClick(NewMasterEventDialog.this,v);
            }
        });
        return  builder.create();
    }

    private String[] getEventCategories(){
        String[] temp = new String[Constants.EventCategories.values().length];

        for(int i=0; i<temp.length ; i++){
            temp[i] = Constants.EventCategories.searchEnumValue(i).name;
        }

        return temp;
    }
}

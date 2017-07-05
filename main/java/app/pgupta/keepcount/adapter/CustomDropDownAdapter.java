package app.pgupta.keepcount.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import app.pgupta.keepcount.R;

/**
 * Created by Home on 7/3/17.
 */

public class CustomDropDownAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context mContext;
    private ArrayList<String> data;

    public CustomDropDownAdapter(Context context, ArrayList<String> data){
        mContext = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(mContext);
        txt.setGravity(Gravity.CENTER);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
       // txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, , 0);
        txt.setText(data.get(position));
        txt.setTextColor(Color.parseColor("#90000000"));
        return  txt;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(mContext);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setText(data.get(position));
        txt.setTextColor(Color.parseColor("#90000000"));
        return  txt;
        //return super.getDropDownView(position, convertView, parent);
    }

}

package app.pgupta.keepcount.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.util.TimeUtil;

/**
 * Created by Home on 7/3/17.
 */

public class ArchivesDialogItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<Event> data;
    Context context;

    public ArchivesDialogItemAdapter(LinkedList<Event> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArchivesDialogItemAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.archive_itemhistory_dialog_list_item, parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ArchivesDialogItemAdapterViewHolder viewHolder = (ArchivesDialogItemAdapterViewHolder)holder;
        Event event = data.get(position);
        viewHolder.tvValue.setText(event.getValue() + " " + event.getUnit());
        int color = -1;
        switch (event.getGain_or_loss()){
            case Event.ValueGainLoss.GAIN: color = ContextCompat.getColor(context,R.color.health);
                break;
            case Event.ValueGainLoss.LOSS: color = ContextCompat.getColor(context,R.color.work);
                break;
            default: color = ContextCompat.getColor(context, R.color.grey);
        }
        viewHolder.tvValue.setTextColor(color);
        viewHolder.tvDesc.setText(event.getDescription());
        viewHolder.tvDate.setText(TimeUtil.getFormattedDateQuickHistory(event.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class ArchivesDialogItemAdapterViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDesc;
        public TextView tvDate;
        public TextView tvValue;

        public ArchivesDialogItemAdapterViewHolder(View itemView) {
            super(itemView);
            tvDate = (TextView) itemView.findViewById(R.id.tvArchivedDialogItemTime);
            tvDesc = (TextView) itemView.findViewById(R.id.tvArchivedDialogItemDesc);
            tvValue = (TextView) itemView.findViewById(R.id.tvArchivedDialogItemValue);
        }
    }
}

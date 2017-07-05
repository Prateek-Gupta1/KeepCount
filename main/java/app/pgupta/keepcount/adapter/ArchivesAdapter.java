package app.pgupta.keepcount.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.dialogbox.ArchivesEventDialog;
import app.pgupta.keepcount.model.ArchivedEvent;
import app.pgupta.keepcount.model.Event;

/**
 * Created by Home on 7/2/17.
 */

public class ArchivesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LinkedList<ArchivedEvent> data;
    private Context context;
    private Fragment fragment;

    public ArchivesAdapter(LinkedList<ArchivedEvent> events, Context ctx, Fragment fragment){
        data = events;
        context = ctx;
        this.fragment = fragment;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ArchivedItemsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.archives_list_item,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ArchivedEvent event = data.get(position);
        ArchivedItemsHolder itemsHolder = (ArchivedItemsHolder)holder;
        int color = -1;
        switch (event.flag){
            case Event.ValueGainLoss.GAIN: color = ContextCompat.getColor(context,R.color.health);
                break;
            case Event.ValueGainLoss.LOSS: color = ContextCompat.getColor(context,R.color.work);
                break;
            default: color = ContextCompat.getColor(context, R.color.grey);
        }
        itemsHolder.tvValue.setTextColor(color);
        itemsHolder.tvTitle.setText(event.getTitle());
        itemsHolder.tvCount.setText("" + event.getEvents().size());
        Event ev = (Event) event.getEvents().get(0);
        itemsHolder.llBackground.setBackgroundResource(ev.getEventCategory().color);
        itemsHolder.tvValue.setText("Total: " + event.getNetValue() + " " + ev.getUnit());
        itemsHolder.bindClickListeners(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class ArchivedItemsHolder extends RecyclerView.ViewHolder{

        public TextView tvCount;
        public TextView tvTitle;
        public TextView tvValue;
      //  public TextView tvExplore;
        public LinearLayout llBackground;

        public ArchivedItemsHolder(View itemView) {
            super(itemView);
            tvCount = (TextView)itemView.findViewById(R.id.tvCount);
            tvTitle = (TextView)itemView.findViewById(R.id.tvArchiveItemTitle);
            tvValue = (TextView)itemView.findViewById(R.id.tvNetValue);
           // tvExplore = (TextView)itemView.findViewById(R.id.tvExplore);
            llBackground = (LinearLayout) itemView.findViewById(R.id.llArchivedItemBG);
        }

        public void bindClickListeners(final int position){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArchivesEventDialog dialog = new ArchivesEventDialog();
                    dialog.setEvent(data.get(position));
                    dialog.setContext(context);
                    dialog.show(fragment.getFragmentManager(),"ArchiveItemDialog");
                }
            });
        }

    }
}

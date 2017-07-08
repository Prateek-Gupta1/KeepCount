package app.pgupta.keepcount.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.model.EventMaster;

/**
 * Created by admin on 5/8/2016.
 */
public class MonthlyTimelineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    LinkedList<Object> data;
    Activity activity;

    public static final int HEADER = 1;
    public static final int EVENT = 2;
    public static final int EMPTY = 3;

    public MonthlyTimelineAdapter(Context context, LinkedList<Object> data, Activity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.this_months_event_dates, parent, false);
                HeaderViewHolder header = new HeaderViewHolder(view);
                return header;
            case EVENT:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.this_months_event_listitem, parent, false);
                EventViewHolder event = new EventViewHolder(view2);
                return event;
            case EMPTY:
                View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.this_months_event_empty,parent,false);
                EmptyViewHolder empty = new EmptyViewHolder(emptyView);
                return empty;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(data.size()==0){
            return;
        }
        if (data.get(position) instanceof String) {

            String date = (String) data.get(position);
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.tvDate.setText(date);

            Log.e("ListData", String.valueOf(position));

        } else {

            Event event = (Event) data.get(position);
            EventViewHolder viewHolder = (EventViewHolder) holder;
            viewHolder.ivIcon.setImageResource(event.getEventCategory().icon);
            //viewHolder.llIconBG.setBackgroundColor(context.getResources().getColor(event.getEventCategory().color));
            viewHolder.llIconBG.setBackgroundResource((event.getEventCategory().color));
            viewHolder.tvDesc.setText(event.getDescription());
            viewHolder.tvTitle.setText(event.getTitle());

           // Log.e("MonthlyTimelineAdapter",event.getUnit());

            viewHolder.tvValue.setText(String.valueOf(event.getValue()) + " " + event.getUnit());
            if (event.getGain_or_loss() == Event.ValueGainLoss.GAIN) {
                viewHolder.tvValue.setTextColor(context.getResources().getColor(R.color.health));
            } else if (event.getGain_or_loss() == Event.ValueGainLoss.LOSS) {
                viewHolder.tvValue.setTextColor(context.getResources().getColor(R.color.work));
            } else {
                viewHolder.tvValue.setTextColor(Color.GRAY);
            }
            viewHolder.bindClickListeners(position);
            Log.e("ListDataView", String.valueOf(position) + " EVENTID: " + String.valueOf(event.getRecordID()));
        }
    }

    @Override
    public int getItemCount() {
        return data.size() > 0 ? data.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0) {
            return EMPTY;
        } else if (data.get(position) instanceof String) {
            return HEADER;
        } else {
            return EVENT;
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvDate;

        public HeaderViewHolder(View view) {
            super(view);
            tvDate = (TextView) view.findViewById(R.id.tvDates);
        }
    }

    private void deleteItem(int position) {
        EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
        dsHandler.openConnection();
        dsHandler.deleteDailyEvent((Event) data.get(position));
        dsHandler.closeConnection();

        Log.e("Before Condition", String.valueOf(position));
        int nextPos = position + 1;
        int prevPos = position - 1;

        if ((nextPos <= data.size() - 1)) {
            Log.e("Position < DataSize", String.valueOf(position));
            if ((data.get(nextPos) instanceof String) && (data.get(prevPos) instanceof String)) {
                data.remove(prevPos);
                Log.e("Position < DataSize", String.valueOf(position));
                notifyItemRemoved(prevPos);
                position--;
                //  notifyItemRangeChanged(prevPos, data.size());
            }
        } else {
            if ((data.get(prevPos) instanceof String)) {
                data.remove(prevPos);
                Log.e("Position > DataSize", String.valueOf(position));
                notifyItemRemoved(prevPos);
                position--;
                // notifyItemRangeChanged(prevPos, data.size());
            }
        }
        Log.e("ThisMonthsEvent", String.valueOf(position));
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, data.size());

    }

    private void updateEvent(Event event, int position) {
        EventDataSourceHandler handler = new EventDataSourceHandler(context);
        handler.openConnection();
        handler.updateDailyEvent(event);
        handler.closeConnection();
        data.set(position, event);
        notifyItemChanged(position);
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvValue;
        public TextView tvDesc;
        public ImageView ivDelete;
        public ImageView ivEdit;
        public ImageView ivIcon;
        public LinearLayout llIconBG;

        public EventViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            tvDesc = (TextView) view.findViewById(R.id.tvDesc);
            tvValue = (TextView) view.findViewById(R.id.tvValue);
            ivEdit = (ImageView) view.findViewById(R.id.ivEditEvent);
            ivDelete = (ImageView) view.findViewById(R.id.ivRemoveEvent);
            llIconBG = (LinearLayout) view.findViewById(R.id.llEventIconBG);
            ivIcon = (ImageView) view.findViewById(R.id.ivEventIcon);
        }

        public void bindClickListeners(final int position) {
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                    builder.setTitle(context.getResources().getString(R.string.master_event_delete_alert_title));
                    builder.setMessage("Sure you want to unmark the event, '" +
                            ((EventMaster) data.get(position)).getTitle() + "' ?");
                    builder.setPositiveButton(R.string.master_event_delete_alert_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteItem(position);
                        }
                    });
                    builder.setNegativeButton(R.string.master_event_delete_alert_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Event event = (Event) data.get(position);

                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
                    LayoutInflater inflater = activity.getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_add_master_event, null);
                    TextView tvHeader = (TextView) dialogView.findViewById(R.id.addEventHeader);
                    TextView tvTitle = (TextView) dialogView.findViewById(R.id.tvEventTitle);
                    final EditText etDesc = (EditText) dialogView.findViewById(R.id.etEventDesc);
                    final EditText etVal = (EditText) dialogView.findViewById(R.id.etValue);
                    TextView tvUnit = (TextView) dialogView.findViewById(R.id.tvUnit);


                    tvHeader.setText("Edit Event");
                    tvTitle.setText(event.getTitle());
                    etDesc.setText(event.getDescription());
                    etVal.setText(String.valueOf(event.getValue()));
                    tvUnit.setText(event.getUnit());

                    Calendar eventDate = Calendar.getInstance();
                    eventDate.setTimeInMillis(Long.valueOf(event.getTimestamp()));

                    final NumberPicker day = (NumberPicker)dialogView.findViewById(R.id.npDay);
                    day.setMinValue(1);
                    day.setMaxValue(eventDate.get(Calendar.DAY_OF_MONTH));
                    // day.setWrapSelectorWheel(false);
                    day.setValue(day.getMaxValue());
                    day.setEnabled(false);

                    final NumberPicker hour = (NumberPicker)dialogView.findViewById(R.id.npHour);
                    hour.setMinValue(0);
                    hour.setMaxValue(23);
                    // hour.setWrapSelectorWheel(false);
                    hour.setValue(eventDate.get(Calendar.HOUR_OF_DAY));
                    hour.setEnabled(false);

                    final NumberPicker min = (NumberPicker)dialogView.findViewById(R.id.npMinute);
                    min.setMinValue(0);
                    min.setMaxValue(59);
                    // min.setWrapSelectorWheel(false);
                    min.setValue(eventDate.get(Calendar.MINUTE));
                    min.setEnabled(false);

                    builder.setView(dialogView);
                    builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            event.setDescription(etDesc.getText().toString());
                            event.setValue(Float.valueOf(etVal.getText().toString()));
                            updateEvent(event, position);
                        }
                    });
                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
    }
}

package app.pgupta.keepcount.adapter;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.dialogbox.DailyEventDialog;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.model.EventMaster;
import app.pgupta.keepcount.model.Reminder;
import app.pgupta.keepcount.receiver.ReminderReceiver;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.TimeUtil;

/**
 * Created by Prateek on 5/4/2016.
 */
public class AllEventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<Object> masterEventsData;
    Context context;
    HashMap<Integer, Reminder> reminders;
    private final int HEADER = 1;
    private final int EVENT = 2;
    private Fragment fragment;
    AlarmManager alarmManager;
    int notificationEventID = -1;

    public interface EventMarkedListener {
        void onEventMarked(Event event);
    }

    EventMarkedListener mMarkedListener;

    public AllEventsAdapter(LinkedList<Object> data, Context context, Fragment fragment, EventMarkedListener listener, int notificationEventID) {
        this.masterEventsData = data;
        this.context = context;
        this.fragment = fragment;
        this.mMarkedListener = listener;
        this.notificationEventID = notificationEventID;
        reminders = initReminders();
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    private HashMap<Integer, Reminder> initReminders() {
        EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
        dsHandler.openConnection();
        HashMap<Integer, Reminder> reminders = dsHandler.getAllReminders();
        dsHandler.closeConnection();
        return reminders;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case HEADER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_header, parent, false);
                HeaderViewHolder header = new HeaderViewHolder(view);
                return header;
            case EVENT:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_list_item, parent, false);
                MasterEventViewHolder event = new MasterEventViewHolder(view2);
                return event;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int pos = position;
        if (masterEventsData.get(position) instanceof String) {

            String header = (String) masterEventsData.get(position);
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.tvHeader.setText(header);
            viewHolder.tvHeader.setTextColor(context.getResources().getColor(Constants.EventCategories.searchEnumValue(header).color));
            // viewHolder.llIconBG.setBackgroundResource(Constants.EventCategories.searchEnumValue(header).color);
            // viewHolder.ivIcon.setImageResource(Constants.EventCategories.searchEnumValue(header).icon);
            //viewHolder.vllBottomBorder.setBackgroundResource(Constants.EventCategories.searchEnumValue(header).color);

        } else {

            EventMaster eventMaster = (EventMaster) masterEventsData.get(position);
            final MasterEventViewHolder viewHolder = (MasterEventViewHolder) holder;
            //viewHolder.ivAddEvent.setImageResource(R.drawabl.ic_add_circle_outline_black_24dp);
            // viewHolder.ivDelete.setImageResource(R.drawable.ic_delete_black_24dp);
            viewHolder.llVerticalBorder.setBackgroundResource(eventMaster.getEventCategory().background);
            viewHolder.vDivider.setBackgroundResource(eventMaster.getEventCategory().color);
            viewHolder.ivIcon.setImageResource(eventMaster.getEventCategory().icon);
            //Log.e("AllEventsAdapter", String.valueOf(eventMaster.getEventCategory().color ));
            viewHolder.tvCreatedOn.setText("Created On : " + TimeUtil.getFormattedDate(eventMaster.getCreatedOn()));
            viewHolder.tvTitle.setText(eventMaster.getTitle());
            if (reminders != null && reminders.containsKey(eventMaster.getId())) {
                Log.e("Reminder", position + " ");
                viewHolder.setReminderSet(true);
                viewHolder.ivReminderIcon.setVisibility(View.VISIBLE);
                Reminder rem = reminders.get(eventMaster.getId());
                viewHolder.tvCreatedOn.setText(TimeUtil.getFormattedDateQuickHistory("" + rem.getTimestamp()));
                viewHolder.tvCreatedOn.setTextSize(10);
            } else {
                viewHolder.setReminderSet(false);
                viewHolder.ivReminderIcon.setVisibility(View.GONE);
            }
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final PopupMenu popupMenu = new PopupMenu(context, viewHolder.ivDelete);

                    if (viewHolder.isReminderSet) {
                        popupMenu.getMenuInflater().inflate(R.menu.menu_overflow_events_reminder, popupMenu.getMenu());
//                        popupMenu.getMenu().findItem(R.id.action_set_reminder).setTitle("Update Reminder");
                    } else {
                        popupMenu.getMenuInflater().inflate(R.menu.menu_overflow_events, popupMenu.getMenu());
                        //   popupMenu.getMenu().findItem(R.id.action_delete_reminder).setVisible(false);
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.action_delete:
                                    popupMenu.dismiss();
                                    viewHolder.actionDeleteEvent(pos);
                                    break;
                                case R.id.action_history:
                                    popupMenu.dismiss();
                                    viewHolder.actionQuickHistory(pos);
                                    break;
                                case R.id.action_set_reminder:
                                    popupMenu.dismiss();
                                    viewHolder.actionSetReminder(pos);
                                    break;
                                case R.id.action_delete_reminder:
                                    popupMenu.dismiss();
                                    viewHolder.actionDeleteReminder(pos);
                                    break;
                                /*case R.id.action_update_reminder:
                                    popupMenu.dismiss();
                                    viewHolder.actionUpdateReminder(pos);*/
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();

                }
            });

            viewHolder.bindClickListeners(position);
            if (eventMaster.getId() == notificationEventID) {
                viewHolder.itemView.performClick();
                notificationEventID = -1;
            }
        }
    }

    @Override
    public int getItemCount() {
        return masterEventsData.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (masterEventsData.get(position) instanceof String) {
            return HEADER;
        } else {
            return EVENT;
        }
        //return super.getItemViewType(position);
    }

    private void deleteItem(int position, MasterEventViewHolder holder) {

        EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
        dsHandler.openConnection();
        dsHandler.deleteMasterEvent((EventMaster) masterEventsData.get(position));
        if (holder.isReminderSet) {
            dsHandler.deleteReminder(((EventMaster) masterEventsData.get(position)).getId());
        }
        dsHandler.closeConnection();

        if (holder.isReminderSet) {
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, ((EventMaster) masterEventsData.get(position)).getId(), intent, 0);
            alarmManager.cancel(alarmIntent);
            reminders.remove(((EventMaster) masterEventsData.get(position)).getId());
        }

        masterEventsData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        //public  LinearLayout llIconBG;
        // public ImageView ivIcon;
        public TextView tvHeader;
        public View vllBottomBorder;

        public HeaderViewHolder(View view) {
            super(view);
            //  llIconBG = (LinearLayout)view.findViewById(R.id.llEventHeaderIconBackground);
            //  ivIcon = (ImageView) view.findViewById(R.id.ivHeaderIcon);
            tvHeader = (TextView) view.findViewById(R.id.tvHeader);
            //vllBottomBorder = view.findViewById(R.id.vheaderBottomBorder);
        }
    }

    public class MasterEventViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle;
        public TextView tvCreatedOn;
        public ImageView ivDelete;
        //  public ImageView ivAddEvent;
        public LinearLayout llVerticalBorder;
        public ImageView ivIcon;
        public ImageView ivReminderIcon;
        public LinearLayout llReminder;
        public LinearLayout vDivider;
        private boolean isReminderSet = false;

        public void setReminderSet(boolean reminderSet) {
            isReminderSet = reminderSet;
        }

        public MasterEventViewHolder(View view) {
            super(view);

            tvTitle = (TextView) view.findViewById(R.id.tvEventTitle);
            tvCreatedOn = (TextView) view.findViewById(R.id.tvCreatedOn);
            //   ivAddEvent = (ImageView) view.findViewById(R.id.ivAddEvent);
            ivDelete = (ImageView) view.findViewById(R.id.ivDeleteEvent);
            llVerticalBorder = (LinearLayout) view.findViewById(R.id.llVerticalBorder);
            ivIcon = (ImageView) view.findViewById(R.id.ivEventItemIcon);
            //21/11/2016
            ivReminderIcon = (ImageView) view.findViewById(R.id.ivReminderIcon);
            ivReminderIcon.setVisibility(View.GONE);
            llReminder = (LinearLayout) view.findViewById(R.id.llReminder);
            vDivider = (LinearLayout) view.findViewById(R.id.llVerticalDivider);
        }

        public void bindClickListeners(final int position) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DailyEventDialog dialog = new DailyEventDialog();
                    dialog.master = (EventMaster) masterEventsData.get(position);
                    dialog.listener = mMarkedListener;
                    dialog.show(fragment.getFragmentManager(), "mark event");
                }
            });

        }

        void actionDeleteEvent(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            builder.setTitle(context.getResources().getString(R.string.master_event_delete_alert_title));
            builder.setMessage("Sure you want to delete the event, '" +
                    ((EventMaster) masterEventsData.get(position)).getTitle() + "' ?");
            builder.setPositiveButton(R.string.master_event_delete_alert_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(position, MasterEventViewHolder.this);
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

        void actionQuickHistory(int position) {

            EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
            dsHandler.openConnection();
            ArrayList<String> history = dsHandler.getQuickHistoryOfEvent(((EventMaster) masterEventsData.get(position)).getId());
            dsHandler.closeConnection();

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            builder.setTitle("Quick History");
            if (history != null) {
                if (history.size() <= 0) {
                    builder.setMessage("You haven't marked it yet");
                } else {
                    builder.setItems(history.toArray(new CharSequence[history.size()]), null);
                }
            }
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();

        }

        void actionDeleteReminder(int position) {
            //Remove reminder from system
            EventMaster master = (EventMaster) masterEventsData.get(position);
            Reminder rem = reminders.get(master.getId());
            // AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, ReminderReceiver.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, rem.getEventID(), intent, 0);
            alarmManager.cancel(alarmIntent);

            //delete reminder from sqlite db
            EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
            dsHandler.openConnection();
            dsHandler.deleteReminder(master.getId());
            dsHandler.closeConnection();

            //remove reminder form the hashmap
            reminders.remove(master.getId());
            notifyItemChanged(position);
        }

        /* void actionUpdateReminder(int position) {

         }
 */
        void actionSetReminder(final int position) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = inflater.inflate(R.layout.event_alarm_dialog, null);
            TextView tvTitle = (TextView) v.findViewById(R.id.tvAlarmTitle);
            final EditText etDate = (EditText) v.findViewById(R.id.etAlarmDate);
            final EditText etTime = (EditText) v.findViewById(R.id.etAlarmTime);

            //etDate.setShowSoftInputOnFocus(false);

            final Calendar reminderTime = Calendar.getInstance();

            tvTitle.setText(((EventMaster) masterEventsData.get(position)).getTitle());
            etDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar currentDate = Calendar.getInstance();
                    int mYear = currentDate.get(Calendar.YEAR);
                    int mMonth = currentDate.get(Calendar.MONTH);
                    int mDay = currentDate.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            monthOfYear += 1;
                            etDate.setText("" + dayOfMonth + " / " + monthOfYear + " / " + year);
                            reminderTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            reminderTime.set(Calendar.MONTH, monthOfYear - 1);
                            reminderTime.set(Calendar.YEAR, year);
                        }
                    }, mYear, mMonth, mDay);
                    mDatePicker.setTitle("Select Date");
                    mDatePicker.show();
                    mDatePicker.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    mDatePicker.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                }
            });

            etTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar currentTime = Calendar.getInstance();
                    int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = currentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            etTime.setText("" + selectedHour + ":" + selectedMinute);
                            reminderTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                            reminderTime.set(Calendar.MINUTE, selectedMinute);
                            reminderTime.set(Calendar.SECOND, 0);
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                    mTimePicker.getButton(TimePickerDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
                    mTimePicker.getButton(TimePickerDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));

                }
            });

            builder.setView(v);

            builder.setPositiveButton(R.string.master_event_delete_alert_yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(reminderTime == null || reminderTime.getTimeInMillis() <= Calendar.getInstance().getTimeInMillis()){
                        Toast.makeText(context,"Cannot set alarm for time before this moment",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Reminder reminder = new Reminder();
                    reminder.setTimestamp(reminderTime.getTimeInMillis());
                    reminder.setEventID(((EventMaster) masterEventsData.get(position)).getId());
                    reminder.setUuid(Constants.getUuid());

                    EventDataSourceHandler dsHandler = new EventDataSourceHandler(context);
                    dsHandler.openConnection();
                    dsHandler.addReminder(reminder);
                    dsHandler.closeConnection();

                    reminders.put(reminder.getEventID(), reminder);
                    notifyItemChanged(position);

                    Intent intent = new Intent(context, ReminderReceiver.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt(Constants.REMINDER_EVENT_ID, reminder.getEventID());
                    bundle.putString(Constants.REMINDER_EVENT_TITLE, ((EventMaster) masterEventsData.get(position)).getTitle());
                    bundle.putInt(Constants.REMINDER_EVENT_ICON, ((EventMaster) masterEventsData.get(position)).getEventCategory().icon);
                    intent.putExtra(Constants.REMINDER_EXTRAS, bundle);

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(context, reminder.getEventID(), intent, 0);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, reminder.getTimestamp(), alarmIntent);
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

    }

}

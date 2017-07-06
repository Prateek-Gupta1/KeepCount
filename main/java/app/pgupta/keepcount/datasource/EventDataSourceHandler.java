package app.pgupta.keepcount.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import app.pgupta.keepcount.model.ArchivedEvent;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.model.EventMaster;
import app.pgupta.keepcount.model.Reminder;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.TimeUtil;


/**
 * Created by Prateek on 5/2/2016.
 */
public class EventDataSourceHandler {

    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] EVENT_MASTER_PROJECTION = {
            EventDBContract.COLUMN_TITLE,
            EventDBContract.TEM_COLUMN_ID,
            EventDBContract.COLUMN_EVENT_CATEGORY,
            EventDBContract.COLUMN_STATUS,
            EventDBContract.COLUMN_GAIN_LOSS,//added 11 sep 2016
            EventDBContract.COLUMN_CREATED_ON,
            EventDBContract.COLUMN_UNIT
    };

    private String[] EVENT_PROJECTION = {

            EventDBContract.COLUMN_EVENT_TIME,
            EventDBContract.COLUMN_DESCRIPTION,
            EventDBContract.COLUMN_GAIN_LOSS,//removed 11 sep 2016
            EventDBContract.COLUMN_VALUE,
            EventDBContract.COLUMN_EVENT_ID

    };

    private final String DB_FILEPATH;

    public EventDataSourceHandler(Context context) {
        dbHelper = new DBHelper(context);
        String packageName = context.getPackageName();
        DB_FILEPATH = "/data/data/" + packageName + "/databases/"+ EventDBContract.DB_NAME;
    }

    public void openConnection() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void closeConnection() {
        database.close();
    }

    public EventMaster createMasterEvent(EventMaster eventMaster) {

        ContentValues values = new ContentValues();
        values.put(EventDBContract.COLUMN_TITLE, eventMaster.getTitle());
        values.put(EventDBContract.COLUMN_CREATED_ON, eventMaster.getCreatedOn());
        values.put(EventDBContract.COLUMN_EVENT_CATEGORY, eventMaster.getEventCategory().value);
        values.put(EventDBContract.COLUMN_UNIT, eventMaster.getUnit());
        values.put(EventDBContract.COLUMN_GAIN_LOSS, eventMaster.getGain_or_loss());// added on 11 sep 2016
        values.put(EventDBContract.COLUMN_STATUS, eventMaster.getStatus());

        long id = database.insert(EventDBContract.TABLE_EVENT_MASTER, null, values);

        Cursor cursor = database.query(
                EventDBContract.TABLE_EVENT_MASTER,
                EVENT_MASTER_PROJECTION,
                EventDBContract.TEM_COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
        EventMaster newmaster = null;
        if (cursor != null) {
            cursor.moveToFirst();
            newmaster = cursorToMasterObject(cursor);
        }
        cursor.close();
        return newmaster;
    }

    public List<EventMaster> listAllMasterEvents() {
        List<EventMaster> masterEvents = new ArrayList<EventMaster>();

        Cursor cursor = database.query(
                EventDBContract.TABLE_EVENT_MASTER,
                EVENT_MASTER_PROJECTION,
                EventDBContract.COLUMN_STATUS + " = ?",
                new String[]{String.valueOf(Constants.STATUS_ACTIVE)},
                null,
                null,
                EventDBContract.COLUMN_EVENT_CATEGORY
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                EventMaster eventMaster = cursorToMasterObject(cursor);
                masterEvents.add(eventMaster);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return masterEvents;
    }

    /**
     * Instead of deleting the Master Event, it flags it as inactive so that past marked events associated with it are visible
     * in reports.
     *
     * @param eventMaster
     */
    public void deleteMasterEvent(EventMaster eventMaster) {
        ContentValues values = new ContentValues();
      /*  values.put(EventDBContract.COLUMN_TITLE, eventMaster.getTitle());
        values.put(EventDBContract.COLUMN_CREATED_ON, eventMaster.getCreatedOn());
        values.put(EventDBContract.COLUMN_EVENT_CATEGORY, eventMaster.getEventCategory().value);
        values.put(EventDBContract.COLUMN_UNIT, eventMaster.getUnit());*/
        values.put(EventDBContract.COLUMN_STATUS, Constants.STATUS_INACTIVE);

        int temp = database.update(
                EventDBContract.TABLE_EVENT_MASTER,
                values,
                EventDBContract.TEM_COLUMN_ID + " = ?",
                new String[]{String.valueOf(eventMaster.getId())}
        );
    }


    public LinkedList<Event> getEventsInDateRange(long startDate, long endDate) {
        LinkedList<Event> dailyEvents = new LinkedList<Event>();

        String query = " SELECT * FROM " +
                EventDBContract.TABLE_DAILY_EVENTS +
                " tde INNER JOIN " +
                EventDBContract.TABLE_EVENT_MASTER +
                " tem ON tde." +
                EventDBContract.COLUMN_EVENT_ID + " = tem." +
                EventDBContract.TEM_COLUMN_ID + " WHERE tde." +
                EventDBContract.COLUMN_EVENT_TIME + " BETWEEN ? AND ? ORDER BY tde." +
                EventDBContract.COLUMN_EVENT_TIME + " desc;";

        Log.e("EVENT_DATA", query);

        String[] selectionArg = new String[]{String.valueOf(startDate), String.valueOf(endDate)};

        Cursor cursor = database.rawQuery(query, selectionArg);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Event event = cursorToDailyEvent(cursor);
                dailyEvents.add(event);
                cursor.moveToNext();
            }
        }

        cursor.close();
        return dailyEvents;
    }

    public LinkedList<ArchivedEvent> getArchivedEventsInDateRange(long startDate, long endDate, int eventCategory) {

        LinkedList<ArchivedEvent> archivedEvents = new LinkedList<>();
        String query = "";
        String[] selectionArg = null;
        if(eventCategory == -1){
            query = " SELECT * FROM " +
                    EventDBContract.TABLE_DAILY_EVENTS +
                    " tde INNER JOIN " +
                    EventDBContract.TABLE_EVENT_MASTER +
                    " tem ON tde." +
                    EventDBContract.COLUMN_EVENT_ID + " = tem." +
                    EventDBContract.TEM_COLUMN_ID + " WHERE tde." +
                    EventDBContract.COLUMN_EVENT_TIME + " BETWEEN ? AND ? "+
                    " ORDER BY tde." + EventDBContract.COLUMN_EVENT_ID + " asc, tde." + EventDBContract.COLUMN_EVENT_TIME + " desc;";

            Log.e("EVENT_DATA ARCHIVED", query);
            selectionArg = new String[]{String.valueOf(startDate), String.valueOf(endDate)};
        }else {
            query = " SELECT * FROM " +
                    EventDBContract.TABLE_DAILY_EVENTS +
                    " tde INNER JOIN " +
                    EventDBContract.TABLE_EVENT_MASTER +
                    " tem ON tde." +
                    EventDBContract.COLUMN_EVENT_ID + " = tem." +
                    EventDBContract.TEM_COLUMN_ID + " WHERE tde." +
                    EventDBContract.COLUMN_EVENT_TIME + " BETWEEN ? AND ? " +
                    " AND " + EventDBContract.COLUMN_EVENT_CATEGORY + " = ? " +
                    " ORDER BY tde." + EventDBContract.COLUMN_EVENT_ID + " asc, tde." + EventDBContract.COLUMN_EVENT_TIME + " desc;";
            selectionArg = new String[]{String.valueOf(startDate), String.valueOf(endDate), String.valueOf(eventCategory)};
        }
        Log.e("EVENT_DATA ARCHIVED", query);

        Cursor cursor = database.rawQuery(query, selectionArg);

        if (cursor != null && cursor.moveToFirst()) {
            int netValue = 0;
            //cursor.moveToNext(); this will move cursor to second row
            Event event = cursorToDailyEvent(cursor);
            netValue += event.getValue();
            ArchivedEvent archivedEvent = new ArchivedEvent(event.getTitle(), event.getId());
            archivedEvent.setFlag(event.getGain_or_loss());
            archivedEvent.events.add(event);
            cursor.moveToNext();

            while (!cursor.isAfterLast()) {
                event = cursorToDailyEvent(cursor);
                if (event.getId() == archivedEvent.getId()) {
                    netValue += event.getValue();
                    archivedEvent.events.add(event);
                } else {
                    archivedEvent.setNetValue(netValue);
                    archivedEvents.add(archivedEvent);
                    netValue = 0;
                    archivedEvent = new ArchivedEvent(event.getTitle(), event.getId());
                    archivedEvent.setFlag(event.getGain_or_loss());
                    archivedEvent.events.add(event);
                    netValue += event.getValue();
                }
                cursor.moveToNext();
            }
            //archivedEvent.setFlag(event.getGain_or_loss());
            archivedEvent.setNetValue(netValue);
            archivedEvents.add(archivedEvent);
            cursor.close();
        }
        return archivedEvents;
    }

    public long createDailyEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventDBContract.COLUMN_EVENT_TIME, event.getTimestamp());
        values.put(EventDBContract.COLUMN_DESCRIPTION, event.getDescription());
        values.put(EventDBContract.COLUMN_EVENT_ID, event.getEventID());
        values.put(EventDBContract.COLUMN_VALUE, event.getValue());
        //values.put(EventDBContract.COLUMN_GAIN_LOSS, event.getGain_or_loss()); removed 11 sep 2016

        long id = database.insert(EventDBContract.TABLE_DAILY_EVENTS, null, values);

        return id;
    }


    public void deleteDailyEvent(Event event) {

        Log.e("DailyEventDelete", String.valueOf(event.getRecordID()));

        int i = database.delete(EventDBContract.TABLE_DAILY_EVENTS,
                EventDBContract.TDE_COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getRecordID())
                });

        Log.e("DeletedCount", String.valueOf(i));
    }

    public void updateDailyEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(EventDBContract.COLUMN_DESCRIPTION, event.getDescription());
        values.put(EventDBContract.COLUMN_VALUE, event.getValue());
        //  values.put(EventDBContract.COLUMN_GAIN_LOSS, event.getGain_or_loss()); removed 11 sep 2016

        int temp = database.update(
                EventDBContract.TABLE_DAILY_EVENTS,
                values,
                EventDBContract.TDE_COLUMN_ID + " = ?",
                new String[]{String.valueOf(event.getRecordID())}
        );
    }

    public ArrayList<String> getQuickHistoryOfEvent(int id) {

        ArrayList<String> history = new ArrayList<>();

        String query = "SELECT "
                + EventDBContract.COLUMN_EVENT_TIME
                + " FROM " + EventDBContract.TABLE_DAILY_EVENTS
                + " WHERE " + EventDBContract.COLUMN_EVENT_ID + " = ?";
        String[] selectionArg = new String[]{String.valueOf(id)};

        Log.e("Quick History query", query + "event ID: " + id);

        Cursor cursor = database.rawQuery(query, selectionArg);
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Log.e("Quick History cursor:",cursor.getString(0));
                history.add(TimeUtil.getFormattedDateQuickHistory(cursor.getString
                        (cursor.getColumnIndex(EventDBContract.COLUMN_EVENT_TIME))));
                cursor.moveToNext();
            }
        }

        return history;
    }

    public HashMap<Integer,Reminder> getAllReminders(){
        deleteObsoleteReminders();

        HashMap<Integer, Reminder> reminders = new HashMap<>();
        String query = "SELECT * FROM " + EventDBContract.TABLE_REMINDERS;
        Cursor cursor = database.rawQuery(query,null);

        if(cursor != null){
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                Reminder reminder = cursorToReminderObject(cursor);
                reminders.put(reminder.getEventID(),reminder);
                cursor.moveToNext();
            }
        }
        return reminders;
    }

    private void deleteObsoleteReminders(){
        int i = database.delete(EventDBContract.TABLE_REMINDERS,
                EventDBContract.COLUMN_REMINDER_TIMESTAMP + " <= ?",
                new String[]{String.valueOf(TimeUtil.getCurrentTimeinMillis())});
    }
    public long addReminder(Reminder reminder){
        ContentValues values = new ContentValues();
        values.put(EventDBContract.COLUMN_REMINDER_TIMESTAMP, reminder.getTimestamp());
        values.put(EventDBContract.COLUMN_REMINDER_UNIQUE_ID,reminder.getUuid());
        values.put(EventDBContract.COLUMN_EVENT_ID, reminder.getEventID());
        long id = database.insert(EventDBContract.TABLE_REMINDERS, null, values);
        return id;
    }

    public int updateReminder(long timestamp, int eventId){
        ContentValues values = new ContentValues();
        values.put(EventDBContract.COLUMN_REMINDER_TIMESTAMP, timestamp);
        int temp = database.update(
                EventDBContract.TABLE_REMINDERS,
                values,
                EventDBContract.COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)}
        );
        return temp;
    }

    public void deleteReminder(int eventId){
        int i = database.delete(EventDBContract.TABLE_REMINDERS,
                EventDBContract.COLUMN_EVENT_ID + " = ?",
                new String[]{String.valueOf(eventId)
                });
    }

    public void backupDataBase() throws FileNotFoundException {
        if(isSDCardWriteable()) {
            FileInputStream fin = new FileInputStream(new File(DB_FILEPATH));
            File fileOut = new File(Environment.getExternalStorageDirectory()+"/keepcount");
        }
    }

    private boolean isSDCardWriteable() {
        boolean rc = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            rc = true;
        }
        return rc;
    }

    private void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }

    private Event cursorToDailyEvent(Cursor cursor) {
        Event event = new Event();
        event.setRecordID(cursor.getInt(cursor.getColumnIndex(EventDBContract.TDE_COLUMN_ID)));
        event.setDescription(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_DESCRIPTION)));
        event.setValue((float) cursor.getDouble(cursor.getColumnIndex(EventDBContract.COLUMN_VALUE)));
        event.setTimestamp(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_EVENT_TIME)));

        event.setGain_or_loss(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_GAIN_LOSS)));// moved on 11 sep 2016
        event.setCreatedOn(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_CREATED_ON)));
        event.setId(cursor.getInt(cursor.getColumnIndex(EventDBContract.TEM_COLUMN_ID)));
        event.setTitle(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_TITLE)));
        event.setEventCategory(Constants.EventCategories.searchEnumValue(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_EVENT_CATEGORY))));
        event.setUnit(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_UNIT)));
        event.setStatus(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_STATUS)));

        return event;
    }

    private EventMaster cursorToMasterObject(Cursor cursor) {
        EventMaster master = new EventMaster();
        master.setCreatedOn(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_CREATED_ON)));
        master.setId(cursor.getInt(cursor.getColumnIndex(EventDBContract.TEM_COLUMN_ID)));
        master.setTitle(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_TITLE)));
        master.setEventCategory(Constants.EventCategories.searchEnumValue(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_EVENT_CATEGORY))));
        master.setUnit(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_UNIT)));
        master.setStatus(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_STATUS)));

        master.setGain_or_loss(cursor.getInt(cursor.getColumnIndex(EventDBContract.COLUMN_GAIN_LOSS)));// added on 11 sep 2016
        return master;
    }

    private Reminder cursorToReminderObject(Cursor cursor){
        Reminder reminder = new Reminder();
        reminder.setRecordID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(EventDBContract.TR_COLUMN_RECORD_ID))));
        reminder.setEventID(Integer.valueOf(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_EVENT_ID))));
        reminder.setTimestamp(Long.valueOf(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_REMINDER_TIMESTAMP))));
        reminder.setUuid(cursor.getString(cursor.getColumnIndex(EventDBContract.COLUMN_REMINDER_UNIQUE_ID)));
        return reminder;
    }

}

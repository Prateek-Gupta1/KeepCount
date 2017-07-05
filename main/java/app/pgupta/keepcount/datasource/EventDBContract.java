package app.pgupta.keepcount.datasource;

/**
 * Created by Prateek on 5/1/2016.
 */
public interface EventDBContract {

    String DB_NAME = "events.db";
    int DATABASE_VERSION = 3;

    String TABLE_EVENT_MASTER = "eventsmaster";
    String TEM_COLUMN_ID = "_id";
    String COLUMN_TITLE = "title";
    String COLUMN_CREATED_ON = "created_on";
    String COLUMN_EVENT_CATEGORY = "event_category";
    String COLUMN_STATUS = "status";
    String COLUMN_UNIT = "units";
    String COLUMN_GAIN_LOSS = "gain_loss";

    String TABLE_DAILY_EVENTS = "dailyevents";
    String TDE_COLUMN_ID = "record_id";
    String COLUMN_EVENT_TIME = "timestamp";
    String COLUMN_DESCRIPTION = "description";
    String COLUMN_VALUE = "value";
    String COLUMN_EVENT_ID = "event_id";

    String CREATE_TABLE_EVENT_MASTER = "create table "
            +TABLE_EVENT_MASTER
            + "("
            + TEM_COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_EVENT_CATEGORY + " integer not null, "
            + COLUMN_UNIT + " text null, "
            + COLUMN_GAIN_LOSS + " integer DEFAULT 2, "//added on 11Sep2016
            + COLUMN_CREATED_ON + " text not null, "
            + COLUMN_STATUS + " integer DEFAULT 1"
            + ");";


    String CREATE_TABLE_DAILY_EVENT = "create table "
            + TABLE_DAILY_EVENTS
            +"("
            + TDE_COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_DESCRIPTION + " text null, "
            + COLUMN_VALUE + " double null, "
            + COLUMN_EVENT_TIME + " text not null, "
            + COLUMN_EVENT_ID + " integer, "
            + "FOREIGN KEY("+COLUMN_EVENT_ID+") references " + TABLE_EVENT_MASTER+"("+TEM_COLUMN_ID+")"
            + ");";

    String TABLE_REMINDERS = "event_reminders";
    String TR_COLUMN_RECORD_ID = "record_id";
    String COLUMN_REMINDER_TIMESTAMP = "reminder_time";
    String COLUMN_REMINDER_UNIQUE_ID = "uuid";
    String CREATE_TABLE__EVENT_REMINDERS = "create table "
            + TABLE_REMINDERS
            + "("
            + TR_COLUMN_RECORD_ID + " integer primary key autoincrement, "
            + COLUMN_REMINDER_TIMESTAMP + " text not null, "
            + COLUMN_EVENT_ID + " integer, "
            + COLUMN_REMINDER_UNIQUE_ID + " text not null, "
            + "FOREIGN KEY("+COLUMN_EVENT_ID+") references " + TABLE_EVENT_MASTER+"("+TEM_COLUMN_ID+")"
            + ");";

}

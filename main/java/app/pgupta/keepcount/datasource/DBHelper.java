package app.pgupta.keepcount.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedHashMap;
import java.util.Locale;

import app.pgupta.keepcount.model.EventMaster;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.TimeUtil;


/**
 * Created by Prateek on 5/1/2016.
 */
public class DBHelper extends SQLiteOpenHelper implements EventDBContract{

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EVENT_MASTER);
        db.execSQL(CREATE_TABLE_DAILY_EVENT);
        db.execSQL(CREATE_TABLE__EVENT_REMINDERS);
        init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EVENT_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_EVENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
        onCreate(db);
    }

    private void init(SQLiteDatabase db){

        ArrayList<EventMaster> eventMasters = coldstartEvents();

        for(EventMaster event : eventMasters){

            ContentValues values = new ContentValues();
            values.put(EventDBContract.COLUMN_TITLE, event.getTitle());
            values.put(EventDBContract.COLUMN_CREATED_ON, event.getCreatedOn());
            values.put(EventDBContract.COLUMN_EVENT_CATEGORY, event.getEventCategory().value);
            values.put(EventDBContract.COLUMN_UNIT, event.getUnit());
            values.put(EventDBContract.COLUMN_STATUS, event.getStatus());
            values.put(EventDBContract.COLUMN_GAIN_LOSS,event.getGain_or_loss());//added 11 sep 2016

            db.insert(EventDBContract.TABLE_EVENT_MASTER,null,values);
        }
    }

    private ArrayList<EventMaster> coldstartEvents(){
        ArrayList<EventMaster> eventMasters = new ArrayList<EventMaster>();

        Locale l = Locale.getDefault();
        Currency currency = Currency.getInstance(l);
        String curr = currency.getCurrencyCode();

        LinkedHashMap<Integer,String[]> initialEvents = new LinkedHashMap<Integer,String[]>();
        initialEvents.put(Constants.EventCategories.HEALTH.value, new String[]{"Visit Doctor",
                "Walk outdoors", "Workout in Gym", "Took daily pills"});
        initialEvents.put(Constants.EventCategories.WORK.value, new String[]{"Attended a meeting",
                "Hosted an event", "Took leave from work", "Finished report"});
        initialEvents.put(Constants.EventCategories.PERSONAL.value, new String[]{"Completed a Book",
                "Dinner at a Restaurant","Borrowed money", "Lent money", "Family outing"});
        initialEvents.put(Constants.EventCategories.HOME.value, new String[]{"Bought Grocery",
                "Paid a bill","Called Cleaning Services","Newspaper not delivered"});
        initialEvents.put(Constants.EventCategories.MISCELLANEOUS.value,new String[]{"Serviced Bike","Mowed lawn", "Painted house"});

        String[] units = {curr,"Calorie","Calorie","Dozes","Minutes","Minutes","Day","Hours","Day",curr,curr,curr,curr,curr,curr,curr,curr,curr,"Minutes","Minutes"};
        int[] gain_loss = {1,1,1,2,1,2,0,1,2,1,0,1,1,1,1,1,0,1,2,1};//added 11 sep 2016
        int i=0;
        for(int key : initialEvents.keySet()){
            String[] titles = initialEvents.get(key);
            for(String title : titles){
             EventMaster event = new EventMaster();
                event.setStatus(1);
                event.setUnit(units[i]);
                event.setGain_or_loss(gain_loss[i]);//added 11 sep 2016
                i++;
                event.setEventCategory(Constants.EventCategories.searchEnumValue(key));
                event.setTitle(title);
                event.setCreatedOn(String.valueOf(TimeUtil.getCurrentTimeinMillis()));
                eventMasters.add(event);
            }
        }
        return eventMasters;
    }
}

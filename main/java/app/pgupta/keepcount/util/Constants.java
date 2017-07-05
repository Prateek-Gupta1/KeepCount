package app.pgupta.keepcount.util;

import java.util.UUID;

import app.pgupta.keepcount.R;

/**
 * Created by admin on 4/23/2016.
 */
public class Constants {

    public static final int STATUS_ACTIVE = 1;
    public static final int STATUS_INACTIVE = 0;
    public static final String FIRST_TIME_LAUNCH = "First_Launch";
    public static final String INSTALLATION_DATE = "install_date";

    public static final String REMINDER_EVENT_ID = "keepcount.event.id";
    public static final String REMINDER_EVENT_TITLE = "keepcount.event.title";
    public static final String REMINDER_EVENT_ICON = "keepcount.event.icon";
    public static final String REMINDER_EXTRAS = "keepcount.reminder.extras";
    public static final int REMINDER_NOTIFICATION_ID = 6473;

    public static String[] MONTHS = new String[]{"Jan","Feb","Mar","April","May","June","July","Aug","Sept","Oct","Nov","Dec"};
    //public static String[] MONTHS_SHORTHAND = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    public static String getUuid(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public enum EventCategories {
        HEALTH(1, R.color.health,"Health", R.drawable.ic_favorite_white_24dp,R.drawable.circle_green),
        WORK(2, R.color.work,"Work",R.drawable.ic_work_white_24dp,R.drawable.circle_red),
        PERSONAL(3,R.color.personal,"Personal",R.drawable.ic_account_circle_white_24dp,R.drawable.circle_blue),
        HOME(4, R.color.home,"Home",R.drawable.ic_home_white_24dp,R.drawable.circle_brown),
        MISCELLANEOUS(5, R.color.misc,"Miscellaneous",R.drawable.ic_gesture_white_24dp,R.drawable.circle_pink);

        public int value;
        public int color;
        public String name;
        public int icon;
        public int background;

        EventCategories(int value, int color, String name, int icon, int background) {
            this.value = value;
            this.color = color;
            this.name = name;
            this.icon = icon;
            this.background = background;
        }

        public static EventCategories searchEnumValue(int value){
            EventCategories cat = EventCategories.MISCELLANEOUS;
            for(EventCategories category: EventCategories.values()){
                if(category.value == value){
                    return category;
                }
            }
            return cat;
        }

        public static EventCategories searchEnumValue(String name){
            EventCategories cat = EventCategories.MISCELLANEOUS;
            for(EventCategories category: EventCategories.values()){
                if(category.name.equalsIgnoreCase(name)){
                    return category;
                }
            }
            return cat;
        }
    }
}

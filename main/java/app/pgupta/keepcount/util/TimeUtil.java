package app.pgupta.keepcount.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by admin on 5/3/2016.
 */
public class TimeUtil {

    TimeUtil(){}

    public static long getCurrentTimeinMillis(){
        Calendar date = Calendar.getInstance();
        return date.getTimeInMillis();
    }

    public static String getFormattedDate(String dateToFormat){
        String dateString = null;
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.valueOf(dateToFormat));
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        dateString = formatter.format(date.getTime());
        return dateString;
    }

    public static String getFormattedDateDaily(String dateToFormat){

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.valueOf(dateToFormat));
        String formatedDate = formatter.format(date.getTime());
        return formatedDate;
    }

    public static String getFormattedDateQuickHistory(String dateToFormat){
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy 'at' HH:mm:ss");
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(Long.valueOf(dateToFormat));
        String formatedDate = formatter.format(date.getTime());
        return formatedDate;
    }

    public static String getMonthString(int mnthNum){
        return Constants.MONTHS[mnthNum-1];
    }

    public static int getMonthNumber(String month){
        String[] months = Constants.MONTHS;
        for(int i=0; i<months.length; i++){
            if(months[i].equals(month)){
                return ++i;
            }
        }
        return -1;
    }
}

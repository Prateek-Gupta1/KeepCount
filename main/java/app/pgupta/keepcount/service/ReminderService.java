package app.pgupta.keepcount.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import app.pgupta.keepcount.activity.MainActivity;
import app.pgupta.keepcount.util.Constants;
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ReminderService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NOTIFY = "keepcount.gmail.coolsunnyster.com.keepcount.reminder.notify";

    private NotificationManager alarmNotificationManager;


    public ReminderService() {
        super("ReminderService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionNotify(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReminderService.class);
        intent.setAction(ACTION_NOTIFY);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, ReminderService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sendNotification(intent);

    }

    private void sendNotification(Intent intent){

        Bundle bundle = intent.getBundleExtra(Constants.REMINDER_EXTRAS);
        Intent intentKeepCount = new Intent(this, MainActivity.class);
        intentKeepCount.putExtra(Constants.REMINDER_EVENT_ID, bundle.getInt(Constants.REMINDER_EVENT_ID));
        intentKeepCount.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Log.e("Reminder Service"," Event ID : " + intentKeepCount.getIntExtra(Constants.REMINDER_EVENT_ID,-1));
        alarmNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentKeepCount, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder alarmNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Keep Count Reminder").setSmallIcon(bundle.getInt(Constants.REMINDER_EVENT_ICON))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(bundle.getString(Constants.REMINDER_EVENT_TITLE)))
                .setContentText(bundle.getString(Constants.REMINDER_EVENT_TITLE));


        alarmNotificationBuilder.setContentIntent(pendingIntent);
        alarmNotificationManager.notify(Constants.REMINDER_NOTIFICATION_ID, alarmNotificationBuilder.build());
       // Log.d("AlarmService", "Notification sent.");
    }
}

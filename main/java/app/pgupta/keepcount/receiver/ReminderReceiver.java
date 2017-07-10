package app.pgupta.keepcount.receiver;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import app.pgupta.keepcount.activity.ThemePreferenceActivity;
import app.pgupta.keepcount.service.ReminderService;

public class ReminderReceiver extends WakefulBroadcastReceiver {
    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        if(pref.getBoolean(ThemePreferenceActivity.PREF_NOTIFICATION_TONE, true)) {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();
        }

        ComponentName comp = new ComponentName(context.getPackageName(), ReminderService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}

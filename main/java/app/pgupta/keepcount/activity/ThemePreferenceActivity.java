package app.pgupta.keepcount.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import app.pgupta.keepcount.R;

/**
 * Created by Home on 7/5/17.
 */

public class ThemePreferenceActivity extends PreferenceActivity {

    public static final int RESULT_CODE_THEME_CHANGED = 1;
    public static final String PREF_NOTIFICATION_TONE = "pref_notification";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.themes);
//        getFragmentManager().beginTransaction().replace(android.R.id.content, new ThemePreferenceFragment()).commit();
//        setResult(RESULT_CODE_THEME_CHANGED);
        findPreference("theme").setOnPreferenceChangeListener(new RefershActivityOnPreferenceChangeListener( RESULT_CODE_THEME_CHANGED));
    }

    public class RefershActivityOnPreferenceChangeListener implements Preference.OnPreferenceChangeListener {
        private final int resultCode;
        public RefershActivityOnPreferenceChangeListener(int resultCode) {
            this.resultCode = resultCode;
        }
        @Override
        public boolean onPreferenceChange(Preference p, Object newValue) {
            setResult(resultCode);
            return true;
        }
    }

//    public static class ThemePreferenceFragment extends PreferenceFragment
//    {
//        @Override
//        public void onCreate(final Bundle savedInstanceState)
//        {
//            super.onCreate(savedInstanceState);
//
//            findPreference("theme").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    return false;
//                }
//            });
//        }
//    }
}

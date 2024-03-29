package app.pgupta.keepcount.activity;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.adapter.AllEventsAdapter;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.fragment.AllEventsFragment;
import app.pgupta.keepcount.fragment.ArchivesFragment;
import app.pgupta.keepcount.fragment.MonthlyTimelineFragment;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.ThemeUtil;
import app.pgupta.keepcount.util.TimeUtil;


public class MainActivity extends AppCompatActivity implements AllEventsAdapter.EventMarkedListener, Serializable {

    private static final String TAG = MainActivity.class.getSimpleName();
    transient private Toolbar toolbar;
    transient private TabLayout tabLayout;
    transient private ViewPager pager;
    transient private SharedPreferences prefs;
    transient private ActionBar mActionBar;
    public static int theme_gradient;

    transient private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String aResponse = msg.getData().getString("message");
            mActionBar.setTitle(aResponse);
        }
    };

    private final int THEME_ACTION = 1;
    private final int REQUEST_EXTERNAL_STORAGE_FOR_BACKUP = 2;
    private final int REQUEST_EXTERNAL_STORAGE_FOR_RESTORE = 3;
    private String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActionBar = getSupportActionBar();
        mActionBar.setTitle("Events and Activities");
        mActionBar.setBackgroundDrawable(ContextCompat.getDrawable(this,ThemeUtil.theme_background_resource));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        //getSupportActionBar().setDisplayUseLogoEnabled(true);

        pager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(pager);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                String title;
                if (position == 0)
                    title = "Events and Activities";
                else if (position == 1)
                    title = "Current Timeline";
                else
                    title = "Archived Counts";
                final String finalTitle = title;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message msg = mHandler.obtainMessage();
                        Bundle b = new Bundle();
                        b.putString("message", finalTitle);
                        msg.setData(b);
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setBackgroundResource(ThemeUtil.theme_background_resource);
        setTabIcons();
    }

    private void init() {
        Intent intent = this.getIntent();
        if (intent.getIntExtra(Constants.REMINDER_EVENT_ID, -1) != -1) {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Constants.REMINDER_NOTIFICATION_ID);
        }
        prefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean isLaunchFirst = prefs.getBoolean(Constants.FIRST_TIME_LAUNCH, true);
        if (isLaunchFirst == true) {
            editor.putString(Constants.INSTALLATION_DATE, String.valueOf(Calendar.getInstance().getTimeInMillis()));
            editor.putBoolean(Constants.FIRST_TIME_LAUNCH, false);
            editor.commit();
        }

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String themeName = pref.getString("theme", Constants.THEME_DEFAULT);
        //setTheme(themeName);
        ThemeUtil.setTheme(themeName);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_event_note_white);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_history_white);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_archive_white);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                startActivityForResult(new Intent(this, ThemePreferenceActivity.class), THEME_ACTION);
                break;
            case R.id.action_backup:
                if (verifyStoragePermissions(this, REQUEST_EXTERNAL_STORAGE_FOR_BACKUP)) {
                    try {
                        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(MainActivity.this);
                        handler.backupDataBase();
                        Toast.makeText(getApplicationContext(), "Backup successful", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), "Backup unsuccessful. Please try again", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_restore:
                if (verifyStoragePermissions(this, REQUEST_EXTERNAL_STORAGE_FOR_RESTORE)) {
                    try {
                        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(MainActivity.this);
                        handler.restoreDataBase();
                        Toast.makeText(getApplicationContext(), "Restore successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_tutorial:
                Intent tutIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutIntent);
                break;
            case R.id.action_about:
                Intent  aboutIntent = new Intent(this, AboutPageActivity.class);
                startActivity(aboutIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == THEME_ACTION) {
            if (resultCode == ThemePreferenceActivity.RESULT_CODE_THEME_CHANGED) {
                finish();
                startActivity(getIntent());
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(AllEventsFragment.newInstance(this), "Events");
        adapter.addFragment(MonthlyTimelineFragment.newInstance(), "Timeline");
        adapter.addFragment(ArchivesFragment.newInstance(), "Archived Count");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onEventMarked(Event event) {
        ViewPagerAdapter adapter = (ViewPagerAdapter) pager.getAdapter();
        MonthlyTimelineFragment frag = (MonthlyTimelineFragment) adapter.getItem(adapter.getTitlePosition("Timeline"));
        String eveDate = TimeUtil.getFormattedDateDaily(event.getTimestamp());
        if (frag.mEventData != null && frag.mEventData.size() != 0 && frag.mEventData.get(0) instanceof String && eveDate.equalsIgnoreCase((String) frag.mEventData.get(0))) {
            frag.mEventData.add(1, event);
            frag.mAdapter.notifyItemInserted(1);
        } else {
            frag.mEventData.add(0, eveDate);
            frag.mEventData.add(1, event);
            frag.mAdapter.notifyDataSetChanged();
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter implements Serializable {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }

        public int getTitlePosition(String title) {
            int i = 0;
            for (String t : mFragmentTitleList) {
                if (t.equalsIgnoreCase(title)) {
                    return i;
                }
                i++;
            }
            return -1;
        }
    }

    private boolean verifyStoragePermissions(Activity activity, int requestCode) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    requestCode
            );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == REQUEST_EXTERNAL_STORAGE_FOR_BACKUP) {
                try {
                    EventDataSourceHandler handler = EventDataSourceHandler.getInstance(MainActivity.this);
                    handler.backupDataBase();
                    Toast.makeText(getApplicationContext(), "Backup successful", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(getApplicationContext(), "Backup unsuccessful. Please try again", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == REQUEST_EXTERNAL_STORAGE_FOR_RESTORE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(MainActivity.this);
                        handler.restoreDataBase();
                        Toast.makeText(getApplicationContext(), "Restore successful", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "Cannot access SD card. Permission denied.", Toast.LENGTH_SHORT).show();
        }
    }
}
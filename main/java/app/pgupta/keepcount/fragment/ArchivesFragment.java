package app.pgupta.keepcount.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.adapter.ArchivesAdapter;
import app.pgupta.keepcount.adapter.CustomDropDownAdapter;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.model.ArchivedEvent;
import app.pgupta.keepcount.util.Constants;

public class ArchivesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private static final String TAG = ArchivesFragment.class.getSimpleName();
    private Spinner spMonths;
    private Spinner spYear;
    private Spinner spCategories;
    private LinkedList<ArchivedEvent> mArchivedEvents;
    private Context mContext;
    private ArchivesAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public ArchivesFragment() {
        // Required empty public constructor
        mArchivedEvents = new LinkedList<>();
    }

    public static ArchivesFragment newInstance() {
        ArchivesFragment fragment = new ArchivesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_archives,container,false);
        spMonths = (Spinner) view.findViewById(R.id.spArdMonth);
        spYear = (Spinner) view.findViewById(R.id.spArdYear);
        spCategories = (Spinner) view.findViewById(R.id.spEventCategory);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvArchives);

        ArrayList<String> months = getArchivedMonths();
        ArrayList<String> years = getArchivedYear();
        String[] cate = new String[] {"All", "Health","Work","Personal","Home","Miscellaneous"};
        List<String> categories = Arrays.asList(cate);

        if(months == null || months.size() == 0){
            spCategories.setVisibility(View.GONE);
            spYear.setVisibility(View.GONE);
            spMonths.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        }else{
            CustomDropDownAdapter monthAdapter = new CustomDropDownAdapter(this.getContext(),months);
            spMonths.setAdapter(monthAdapter);
            CustomDropDownAdapter yearAdapter = new CustomDropDownAdapter(this.getContext(), years);
            spYear.setAdapter(yearAdapter);
            CustomDropDownAdapter categAdapter = new CustomDropDownAdapter(this.getContext(),new ArrayList<>(categories));
            spCategories.setAdapter(categAdapter);

            spMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadData(spMonths.getSelectedItem().toString(),
                             spYear.getSelectedItem().toString(),
                             spCategories.getSelectedItem().toString());
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            spYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadData(spMonths.getSelectedItem().toString(),
                             spYear.getSelectedItem().toString(),
                             spCategories.getSelectedItem().toString());
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            spCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    loadData(spMonths.getSelectedItem().toString(),
                            spYear.getSelectedItem().toString(),
                            spCategories.getSelectedItem().toString());
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            loadData(spMonths.getSelectedItem().toString(),
                     spYear.getSelectedItem().toString(),
                     spCategories.getSelectedItem().toString());
            mAdapter = new ArchivesAdapter(mArchivedEvents,this.getContext(),ArchivesFragment.this);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));
            RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
            itemAnimator.setAddDuration(500);
            itemAnimator.setChangeDuration(500);
            mRecyclerView.setItemAnimator(itemAnimator);
        }

        return view;
    }

    private void loadData(String month, String year, String eventCategory) {
        mRecyclerView.setVisibility(View.VISIBLE);
        try {
            int categoryId = -1;
            if(!eventCategory.equalsIgnoreCase("all")){
                categoryId = Constants.EventCategories.searchEnumValue(eventCategory).value;
            }
            int monthNumber = 0;
            String[] months = Constants.MONTHS;
            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(month)) {
                    monthNumber = i + 1;
                    break;
                }
            }

            int _year = Integer.valueOf(year).intValue();
            Calendar startDate = Calendar.getInstance();
            startDate.set(_year, monthNumber - 1, 1);
            startDate.set(Calendar.HOUR_OF_DAY,0);
            int endDay = startDate.getMaximum(Calendar.DAY_OF_MONTH);

            Calendar endDate = Calendar.getInstance();
            endDate.set(_year, monthNumber - 1, endDay);
            EventDataSourceHandler handler = EventDataSourceHandler.getInstance(getContext());
            handler.openConnection();
            mArchivedEvents.clear();
            mArchivedEvents.addAll(handler.getArchivedEventsInDateRange(startDate.getTimeInMillis(), endDate.getTimeInMillis(),categoryId));
            handler.closeConnection();
            if(mArchivedEvents.size() == 0){
                mRecyclerView.setVisibility(View.GONE);
            }
        }catch(Exception e){
            Log.e(TAG,"exception in load data"+ e.getMessage());
        }
    }


    private ArrayList<String> getArchivedMonths() {
        ArrayList<String> months = null;
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String date = pref.getString(Constants.INSTALLATION_DATE, null);
        if (date != null) {
            Calendar time = Calendar.getInstance();
            int currentMonth = time.get(Calendar.MONTH);
            //currentMont;h++
            time.setTimeInMillis(Long.valueOf(date));
            int installedMonth = time.get(Calendar.MONTH);
            //installedMonth++;
            if (currentMonth > installedMonth) {
                months = new ArrayList<String>();
                while (installedMonth < currentMonth) {
                    months.add(Constants.MONTHS[installedMonth++]);
                    //installedMonth++;
                }
            }
        }

        return months;
    }

    private ArrayList<String> getArchivedYear() {
        ArrayList<String> years = null;
        SharedPreferences pref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String date = pref.getString(Constants.INSTALLATION_DATE, null);
        if (date != null) {
            Calendar time = Calendar.getInstance();
            int currentYear = time.get(Calendar.YEAR);
            time.setTimeInMillis(Long.valueOf(date));
            int installedYear = time.get(Calendar.YEAR);
            if (currentYear >= installedYear) {
                years = new    ArrayList<>();
                while (installedYear <= currentYear) {
                    years.add(String.valueOf(installedYear));
                    installedYear++;
                }
            }
        }
        return years;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

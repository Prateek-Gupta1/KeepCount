package app.pgupta.keepcount.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;
import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.adapter.MonthlyTimelineAdapter;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.util.TimeUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MonthlyTimelineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MonthlyTimelineFragment extends Fragment {

    RecyclerView mRecyclerView;
    public LinkedList<Object> mEventData;
    public MonthlyTimelineAdapter mAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MonthlyTimelineFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MonthlyTimelineFragment newInstance() {
        MonthlyTimelineFragment fragment = new MonthlyTimelineFragment();
        return fragment;
    }

    public MonthlyTimelineFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_this_months_event, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewThisMonth);
        //eventData = loadData(v.getContext());
        mEventData = loadData();
        mAdapter = new MonthlyTimelineAdapter(getContext(),mEventData,getActivity());
        mRecyclerView.setAdapter(mAdapter);
        GridLayoutManager glm = new GridLayoutManager(this.getContext(),2);
        glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)){
                    case MonthlyTimelineAdapter.HEADER:
                        return 2;
                    case MonthlyTimelineAdapter.EVENT:
                        if((position + 1 < mAdapter.getItemCount())
                                && (mAdapter.getItemViewType(position + 1) == MonthlyTimelineAdapter.EVENT)
                                || mAdapter.getItemViewType(position - 1) == MonthlyTimelineAdapter.EVENT)
                            return 1;
                        else
                            return 2;
                    default:
                        return 2;
                }
            }
        });
       // mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setLayoutManager(glm);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(400);
        mRecyclerView.setItemAnimator(itemAnimator);
        return v;
    }

    private LinkedList<Object> loadData(){
        LinkedList<Object> data = new LinkedList<Object>();
        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(getContext());
        handler.openConnection();
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.DAY_OF_MONTH, 1);
        startDate.set(Calendar.HOUR_OF_DAY,0);
        LinkedList<Event> tempList = handler.getEventsInDateRange(startDate.getTimeInMillis(), Calendar.getInstance().getTimeInMillis());
        //data.add(TimeUtil.getFormattedDateDaily(String.valueOf(Calendar.getInstance().getTimeInMillis())));
        handler.closeConnection();
        int current_day = -1;
        for(Event event : tempList){
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(Long.valueOf(event.getTimestamp()));
            if(current_day != date.get(Calendar.DAY_OF_MONTH)){
                data.add(TimeUtil.getFormattedDateDaily(String.valueOf(date.getTimeInMillis())));
                data.add(event);
                current_day = date.get(Calendar.DAY_OF_MONTH);
            }else{
                data.add(event);
            }
        }
        return data;
    }

}

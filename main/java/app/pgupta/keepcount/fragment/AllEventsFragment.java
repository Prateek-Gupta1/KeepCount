package app.pgupta.keepcount.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.LinkedList;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.activity.MainActivity;
import app.pgupta.keepcount.adapter.AllEventsAdapter;
import app.pgupta.keepcount.datasource.EventDataSourceHandler;
import app.pgupta.keepcount.dialogbox.NewMasterEventDialog;
import app.pgupta.keepcount.model.Event;
import app.pgupta.keepcount.model.EventMaster;
import app.pgupta.keepcount.util.Constants;
import app.pgupta.keepcount.util.TimeUtil;


public class AllEventsFragment extends Fragment implements NewMasterEventDialog.NewMasterEventDialogListener {

    private static final String EVENT_MARKED_LISTENER = "listener";

    private LinkedList<Object> masterEventData;
    private FloatingActionButton actionButton;
    RecyclerView mRecyclerView;
    AllEventsAdapter mAdapter;
    LinkedList<Object> mData;
    AllEventsAdapter.EventMarkedListener mMarkedListener;
    private static int notificationEventID;
    int posit = -1;

    public AllEventsFragment() {
        // Required empty public constructor
    }

    public static AllEventsFragment newInstance(MainActivity listener) {
        Intent listenerIntent = listener.getIntent();
        notificationEventID = listenerIntent.getIntExtra(Constants.REMINDER_EVENT_ID,-1);
        listenerIntent.putExtra(Constants.REMINDER_EVENT_ID, -1);
        AllEventsFragment fragment = new AllEventsFragment();
        Bundle args = new Bundle();
        args.putSerializable(EVENT_MARKED_LISTENER, listener);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMarkedListener = (AllEventsAdapter.EventMarkedListener) getArguments().getSerializable(EVENT_MARKED_LISTENER);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_events, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.recyclerview);
        masterEventData = loadData(v.getContext());
        mAdapter = new AllEventsAdapter(masterEventData, v.getContext(), AllEventsFragment.this, mMarkedListener, notificationEventID);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(v.getContext()));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        // itemAnimator.setRemoveDuration(1000);
        mRecyclerView.setItemAnimator(itemAnimator);

        actionButton = (FloatingActionButton) v.findViewById(R.id.fab);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMasterEventDialog dialog = new NewMasterEventDialog();
                dialog.mListener = AllEventsFragment.this;
                dialog.show(AllEventsFragment.this.getFragmentManager(), "create New event");
            }
        });


        for(Object obj: masterEventData){
            posit++;
            if(obj instanceof EventMaster){
                if(((EventMaster)obj).getId() == notificationEventID){
                    mRecyclerView.scrollToPosition(posit-1);
                    notificationEventID = -1;
                }
            }
        }

        return v;
    }

    private LinkedList<Object> loadData(Context context) {

        mData = new LinkedList<>();

        EventDataSourceHandler dsHandler = EventDataSourceHandler.getInstance(context);
        dsHandler.openConnection();
        ArrayList<EventMaster> masterEvents = (ArrayList<EventMaster>) dsHandler.listAllMasterEvents();
        dsHandler.closeConnection();

        int value = -1;
        for (EventMaster event : masterEvents) {
            if (value != event.getEventCategory().value) {
                mData.add(event.getEventCategory().name);
                mData.add(event);
            } else {
                mData.add(event);
            }
            value = event.getEventCategory().value;
        }
        return mData;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, View view) {
        EventMaster event = new EventMaster();
        event.setCreatedOn(String.valueOf(TimeUtil.getCurrentTimeinMillis()));
        event.setStatus(Constants.STATUS_ACTIVE);
        event.setTitle(((EditText) view.findViewById(R.id.etEventTitle)).getText().toString());
        //event.setGain_or_loss();
        event.setEventCategory(
                Constants.EventCategories.searchEnumValue(
                        (
                                (Spinner) view.findViewById(R.id.spEventCategory)
                        ).getSelectedItem().toString()
                )
        );
        String unit  = ((EditText) view.findViewById(R.id.etUnit)).getText().toString();

        if(unit == null || "".equals(unit)){
            event.setUnit("unit");
        }else{
            event.setUnit(unit);
        }
        //added 11 sep 2016
        int id = ((RadioGroup)view.findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (id){
            case R.id.rbNone:
                event.setGain_or_loss(Event.ValueGainLoss.NONE);
                break;
            case R.id.rbGain:
                event.setGain_or_loss(Event.ValueGainLoss.GAIN);
                break;
            case R.id.rbLost:
                event.setGain_or_loss(Event.ValueGainLoss.LOSS);
        }
        //event.setUnit((unit != null && !"".equals(unit)) ? unit:"None");
        EventDataSourceHandler handler = EventDataSourceHandler.getInstance(getContext());
        handler.openConnection();
        int position = putEventInList(handler.createMasterEvent(event));
        handler.closeConnection();
        mAdapter.notifyDataSetChanged();
        //mMarkedListener.onEventAdded(event);
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, View view) {
        return;
    }

    int putEventInList(EventMaster event) {
        int pos = -1;
        for (int i = 0; i < mData.size(); i++) {
            if ((mData.get(i) instanceof String) && mData.get(i).equals(event.getEventCategory().name)) {
                mData.add(++i, event);
                pos = i;
            }
        }

        if(pos == -1){
            mData.add(event.getEventCategory().name);
            mData.add(event);
            pos +=2;
        }
        return pos;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}

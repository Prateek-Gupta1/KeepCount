package app.pgupta.keepcount.dialogbox;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.pgupta.keepcount.R;
import app.pgupta.keepcount.adapter.ArchivesDialogItemAdapter;
import app.pgupta.keepcount.model.ArchivedEvent;
import app.pgupta.keepcount.model.Event;

/**
 * Created by Home on 7/3/17.
 */

public class ArchivesEventDialog extends DialogFragment {

    private ArchivedEvent event;
    private Context context;
    private ArchivesDialogItemAdapter mAdapter;
    private Dialog dialog;

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View v  = inflater.inflate(R.layout.dialog_archive_item_history, null);

        TextView tvTitle = (TextView) v.findViewById(R.id.tvArchivedDialogTitle);
        LinearLayout llHeaderBG = (LinearLayout) v.findViewById(R.id.llArchivedDialogHeaderBG);
        ImageView ivIcon = (ImageView) v.findViewById(R.id.ivArchivedDialogHeaderIcon);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rvArchivedDialogBody);

        Event item = event.events.get(0);
        tvTitle.setText(item.getTitle());
        llHeaderBG.setBackgroundResource(item.getEventCategory().color);
        ivIcon.setImageResource(item.getEventCategory().icon);
        mAdapter = new ArchivesDialogItemAdapter(event.events,context);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        builder.setView(v);
        builder.setPositiveButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        return dialog;
    }

    public void setEvent(ArchivedEvent event){
        this.event = event;
    }

    public void setContext(Context context){
        this.context = context;
    }
}

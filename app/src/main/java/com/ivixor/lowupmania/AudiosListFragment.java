package com.ivixor.lowupmania;


import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import java.util.ArrayList;
import java.util.List;

public class AudiosListFragment extends ListFragment implements EditAudiosAsyncTask.AsyncResponseListener {

    public final static String TAG = "AudiosListFragment";

    private LinearLayout progressBarView;
    private ListView listView;

    private List<Song> audios;
    private AudiosArrayAdapter mAdapter;

    private ProgressDialog progressDialog;

    private boolean disableActions = false;

    public AudiosListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        //DataWrapper dw = (DataWrapper) getArguments().getSerializable("audios");
        //audios = dw.getAudiosData();

        audios = new ArrayList<Song>();

        mAdapter = new AudiosArrayAdapter(getActivity(), audios);

        //progressBarView = (LinearLayout) getView().findViewById(R.id.view_progressbar);

        listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = listView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " Selected");
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.menu_itemselector, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_low:
                        int checked1 = listView.getCheckedItemCount();
                        Toast.makeText(getActivity(), "low " + checked1, Toast.LENGTH_SHORT).show();
                        //service.doWork(audios, true);
                        return true;
                    case R.id.action_up:
                        int checked2 = listView.getCheckedItemCount();
                        Toast.makeText(getActivity(), "up " + checked2, Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                mAdapter.removeSelection();
            }
        });


        listView.setAdapter(mAdapter);

        ((LoginActivity) getActivity()).bind();
        ((LoginActivity) getActivity()).getAudiosList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_audioslist, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actions_audioslist, menu);
        menu.findItem(R.id.action_low).setEnabled(!disableActions).setVisible(!disableActions);
        menu.findItem(R.id.action_up).setEnabled(!disableActions).setVisible(!disableActions);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_low:
                Toast.makeText(getActivity(), "low", Toast.LENGTH_SHORT).show();
                showProgressBar();
                ((LoginActivity) getActivity()).editAudios(audios, true);
                return true;
            case R.id.action_up:
                Toast.makeText(getActivity(), "up", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onProcessFinished() {
        //setAdapter();
    }

    public void updateData(List<Song> audios) {
        this.audios = audios;
        mAdapter.clear();
        mAdapter.addAll(audios);
        mAdapter.notifyDataSetChanged();
    }

    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showProgressBar() {
        dismissProgressDialog();

        progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                "Editing...", true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {

                    }
                }
        );
    }

    public void toggleActions(boolean disable) {
        disableActions = disable;
        getActivity().invalidateOptionsMenu();
    }

    private class AudiosArrayAdapter extends ArrayAdapter<Song> {

        private SparseBooleanArray selectedIDs;

        private class ViewHolder {
            TextView songName;
        }

        public AudiosArrayAdapter(Context context, List<Song> objects) {
            super(context, R.layout.item_audio, objects);

            selectedIDs = new SparseBooleanArray();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Song song = getItem(position);

            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_audio, parent, false);
                viewHolder.songName = (TextView) convertView.findViewById(R.id.tvAudioName);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.songName.setText(song.getArtist() + " - " + song.getTitle());

            return convertView;
        }

        public void toggleSelection(int position) {
            selectView(position, !selectedIDs.get(position));
        }

        public void selectView(int position, boolean value) {
            if (value) {
                selectedIDs.put(position, value);
            } else {
                selectedIDs.delete(position);
            }

            notifyDataSetChanged();
        }

        public int getSelectedCount() {
            return selectedIDs.size();
        }

        public SparseBooleanArray getSelectedIDs() {
            return selectedIDs;
        }

        public void removeSelection() {
            selectedIDs = new SparseBooleanArray();
            notifyDataSetChanged();
        }
    }
}

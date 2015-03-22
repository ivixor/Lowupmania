package com.ivixor.lowupmania;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;


public class AudiosListActivity extends ListActivity implements EditAudiosAsyncTask.AsyncResponseListener, RequestHandler.RequestHandlerListener {

    private ListView listView;

    private List<Song> audios;

    private SongsArrayAdapter mAdapter;

    private EditAudiosAsyncTask editAudiosAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("audios");
        audios = dw.getSongsData();

        mAdapter = new SongsArrayAdapter(this, audios);

        setUpProgressBar();

        listView = getListView();
        listView.setAdapter(mAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                final int checkedCount = listView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " Selected");

                mAdapter.toggleSelection(i);

                //listView.setItemChecked(i, true);

                //mAdapter.toggleSelection(position, menuItem);

                getActionBar().setDisplayHomeAsUpEnabled(true);

                Toast.makeText(getApplicationContext(), "" + i, Toast.LENGTH_SHORT).show();
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
                        return true;
                    case R.id.action_up:
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

        editAudiosAsyncTask = new EditAudiosAsyncTask(this, audios);
        editAudiosAsyncTask.delegate = this;
    }

    public void setUpProgressBar() {
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER)
        );
        progressBar.setIndeterminate(true);

        //getListView().setEmptyView(progressBar);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.addView(progressBar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_audioslist, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_low:
                editAudiosAsyncTask.execute(true);
                return true;
            case R.id.action_up:
                editAudiosAsyncTask.execute(false);
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, final View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);

        //v.setSelected(true);
        /*final Song song = (Song) l.getItemAtPosition(position);
        v.animate().setDuration(2000).alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        audios.remove(song);
                        mAdapter.notifyDataSetChanged();
                        v.setAlpha(1);
                    }
                });*/
    }

    @Override
    public void onProcessFinished() {
        RequestHandler handler = new RequestHandler(this);
        handler.getAudios();
    }

    @Override
    public void onRequestFinished(List<Song> audios) {
        mAdapter.notifyDataSetChanged();
    }

    private class SongsArrayAdapter extends ArrayAdapter<Song> {

        private SparseBooleanArray selectedIDs;

        private class ViewHolder {
            TextView songName;
        }

        public SongsArrayAdapter(Context context, List<Song> objects) {
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

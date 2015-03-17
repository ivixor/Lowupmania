package com.ivixor.lowupmania;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.util.SimpleArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AudiosListActivity extends ListActivity {

    private List<Song> songs;
    private SongsArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("songs");
        songs = dw.getSongsData();

        mAdapter = new SongsArrayAdapter(this, songs);

        /*ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER)
        );
        progressBar.setIndeterminate(true);*/
        //getListView().setEmptyView(progressBar);
        getListView().setAdapter(mAdapter);

        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        //root.addView(progressBar);

        /*String[] fromColumns = { "audio" };
        int[] toViews = { android.R.id.text1 };

        mAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_1, null,
                fromColumns, toViews, 0);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);*/
    }

    @Override
    protected void onListItemClick(ListView l, final View v, int position, long id) {
        //super.onListItemClick(l, v, position, id);

        final Song song = (Song) l.getItemAtPosition(position);
        v.animate().setDuration(2000).alpha(0)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        songs.remove(song);
                        mAdapter.notifyDataSetChanged();
                        v.setAlpha(1);
                    }
                });
    }

    private class SongsArrayAdapter extends ArrayAdapter<Song> {

        private HashMap<Song, Integer> mIdMap = new HashMap<Song, Integer>();

        public SongsArrayAdapter(Context context, List<Song> objects) {
            super(context, 0, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Song song = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_audio, parent, false);
            }

            ((TextView) convertView.findViewById(R.id.audioName)).setText(song.getArtist() + " - " + song.getTitle());

            return convertView;
        }
    }
}

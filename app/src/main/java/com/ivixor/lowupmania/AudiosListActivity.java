package com.ivixor.lowupmania;

import android.app.ActionBar;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.SimpleArrayMap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.List;


public class AudiosListActivity extends ListActivity {

    private List<Song> songs;
    private SongsArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        private class ViewHolder {
            TextView songName;
        }

        public SongsArrayAdapter(Context context, List<Song> objects) {
            super(context, R.layout.item_audio, objects);
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
    }
}

package com.ivixor.lowupmania;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ListFragment;
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


/*
public class AudiosListActivity extends ListFragment implements EditAudiosAsyncTask.AsyncResponseListener, RequestHandler.RequestHandlerListener {

    private ListView listView;

    private List<Song> audios;

    private SongsArrayAdapter mAdapter;

    private EditAudiosAsyncTask editAudiosAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getActionBar().setDisplayHomeAsUpEnabled(true);

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
    public void onProcessFinished() {
        RequestHandler handler = new RequestHandler(this);
        handler.getAudios();
    }

    @Override
    public void onRequestFinished(List<Song> audios) {
        mAdapter.notifyDataSetChanged();
    }


}
*/

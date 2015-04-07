package com.ivixor.lowupmania;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.List;


public class ExecuteQueryAsyncTask extends AsyncTask<Void, Void, Void> {

    private AudiosDataSource datasource;
    private Context context;
    private ProgressDialog progressDialog;

    private String operation = null;

    private List<Song> audios;

    public AsyncResponseListener listener = null;

    public ExecuteQueryAsyncTask(Context context, String operation) {
        this.context = context;
        this.operation = operation;
    }

    public interface AsyncResponseListener {
        void onQueryFinished(List<Song> values);
    }

    public void setData(List<Song> audios) {
        this.audios = audios;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (operation.equals("backup")) {
            makeBackup();
        } else if (operation.equals("restore")) {
            restoreFromBackup();
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener = (AsyncResponseListener) context;

        datasource = new AudiosDataSource(context);

        String progressTitle = null;

        if (operation.equals("backup")) {
            progressTitle = "Making audios backup...";
        } else if (operation.equals("restore")) {
            progressTitle = "Restoring audios backup...";
        }

        progressDialog = ProgressDialog.show(context, "Press back to cancel",
                progressTitle, true, true,
                new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        cancel(true);
                    }
                }
        );
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onQueryFinished(audios);
    }

    private void makeBackup() {
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        datasource.deleteAllEntries();

        for (Song audio : audios) {
            if (!isCancelled()) {
                datasource.createEntry(audio);
                Log.d("entry", audio.toString());
            } else {
                datasource.deleteAllEntries();
            }
        }

        datasource.close();
        Toast.makeText(context, "db closed", Toast.LENGTH_SHORT).show();
    }

    private void restoreFromBackup() {
        try {
            datasource.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        audios = datasource.getAllEntries();

        datasource.close();
    }
}

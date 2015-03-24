package com.ivixor.lowupmania;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKAbstractOperation;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class EditAudiosAsyncTask extends AsyncTask<Boolean, Integer, Void> {

    private Context context;

    private Song audio;
    private List<Song> audios;
    private List<Song> failed = new ArrayList<Song>();

    private String err = null;

    private ProgressDialog progressDialog;
    private boolean showProgress;

    private boolean isRequestDone = false;

    public interface AsyncResponseListener {
        void onProcessFinished();
    }

    public AsyncResponseListener delegate = null;

    public EditAudiosAsyncTask(Context context, List<Song> audios) {
        this.context = context;
        this.audios = audios;
    }

    public void startProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Downloading file(s)...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
    }

    @Override
    protected Void doInBackground(Boolean... params) {

        if (!isCancelled()) {
            boolean toLower = params[0];

            long start = System.currentTimeMillis();

            showProgress = true;

            sendRequest(toLower);
            //sendTestRequest(audios);

            /*while (isError) {
                err = "reedit is needed";
                isError = false;
                sendRequest(failed);
                failed.clear();
            }*/
            Log.d("edit response", "" + (System.currentTimeMillis() - start));
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        failed = new ArrayList<Song>();
        startProgressDialog();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (err != null) {
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
            delegate.onProcessFinished();
        } else {
            Toast.makeText(context, "nice", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (showProgress) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

            progressDialog.setProgress(progress[0]);
            progressDialog.setMessage("Loading " + (audio.getArtist()) + " - " + audio.getTitle());

            if (progressDialog.getProgress() >= 100) {
                progressDialog.setProgress(100);
                progressDialog.setMessage("Done!");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                    }
                }, 1000);

                showProgress = false;
            }
        }
    }

    private void sendRequest(boolean toLower) {
        if (audios == null || audios.isEmpty()) {
            return;
        }

        for (int i = 0; i < audios.size(); i++) {
            audio = audios.get(i);

            String artist;
            String title;

            if (toLower) {
                artist = audio.getArtist().toLowerCase();
                title = audio.getTitle().toLowerCase();
            } else {
                artist = audio.getArtist().toUpperCase();
                title = audio.getTitle().toUpperCase();
            }

            final VKRequest request = new VKRequest("audio.edit", VKParameters.from(
                    "audio_id", audio.getId(),
                    "owner_id", audio.getOwnerId(),
                    "artist", artist,
                    "title", title)
            );

            request.useSystemLanguage = false;
            request.attempts = 0;

            VKJsonOperation operation = (VKJsonOperation) request.getOperation();
            operation.start();
            String response = operation.getResponseJson().toString();
            Log.d("edit response", i + " - " + response);

            publishProgress(((i + 1) * 100) / audios.size());

            Utils.pause(300);
        } // https://api.vk.com/method/audio.edit?aid=' + aid + '&owner_id' + owner_id + '&artist=' + artist + '&title=' + '&no_search=' + 0 + '&access_token=' + accToken
    }
}

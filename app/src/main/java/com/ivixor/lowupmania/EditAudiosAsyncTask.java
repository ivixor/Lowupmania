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

    public interface AsyncResponseListener {
        void onProcessFinished();
    }

    public AsyncResponseListener delegate = null;

    public EditAudiosAsyncTask(Context context, List<Song> audios) {
        this.context = context;
        this.audios = audios;
    }

    @Override
    protected Void doInBackground(Boolean... params) {

        if (!isCancelled()) {

        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        failed = new ArrayList<Song>();
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

    }


}

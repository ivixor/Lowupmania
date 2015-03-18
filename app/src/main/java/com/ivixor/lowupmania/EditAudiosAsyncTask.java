package com.ivixor.lowupmania;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.List;


public class EditAudiosAsyncTask extends AsyncTask<List<Song>, Void, Void> {

    private Context context;

    private Song test;

    private String err = null;
    private int current = 0;

    public interface AsyncResponseListener {
        void processFinished();
    }

    public AsyncResponseListener delegate = null;

    private VKRequest.VKRequestListener editAudioRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            Log.d("edit response", "cool - " + current);

        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            Log.d("edit response", "attempt: " + current + request.response.toString());
        }

        @Override
        public void onError(VKError error) {
            Log.d("edit response", test.getArtist() + " - " + test.getTitle());
        }
    };

    public EditAudiosAsyncTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(List<Song>... params) {

        List<Song> audios = params[0];

        long start;
        long end;

        for (int i = 0; i < audios.size(); i++) {

            start = System.currentTimeMillis();

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }

            end = System.currentTimeMillis();
            Log.d("time", "" + (end - start));

            test = audios.get(i);
            current = i;

            final VKRequest request = new VKRequest("audio.edit", VKParameters.from(
                    "audio_id", test.getId(),
                    "owner_id", test.getOwnerId(),
                    "artist", test.getArtist().toLowerCase(),
                    "title", test.getTitle().toLowerCase())
            );

            //Log.d("edit response", audios.get(i).getTitle());

            request.secure = false;
            request.useSystemLanguage = false;
            request.attempts = 3;
            request.executeWithListener(editAudioRequestListener);


        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (err != null) {
            Toast.makeText(context, err, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "nice", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

}

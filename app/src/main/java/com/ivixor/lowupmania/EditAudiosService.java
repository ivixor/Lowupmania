package com.ivixor.lowupmania;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import java.util.ArrayList;
import java.util.List;


public class EditAudiosService extends Service {

    public final static String TAG = "EditAudiosService";

    private IBinder binder = new MyBinder();

    private List<Song> audios;
    private List<Song> failed;

    private ProgressDialog progressDialog;
    private boolean showProgress = false;
    private boolean isCancelled = false;


    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public EditAudiosService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public void cancel() {
        Log.d(TAG, "cancelled");
        cancelNotification();
        done();
        isCancelled = true;
    }

    private void setupNotification() {
        Intent cancelIntent = new Intent(this, LoginActivity.class);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Lowupmania")
                .setContentText("Editing...")
                .setAutoCancel(true)
                .addAction(R.drawable.ic_launcher, "Cancel", pendingCancelIntent);

        Intent resultIntent = new Intent(this, LoginActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(LoginActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void updateNotification(int percentage, Song audio) {
        mBuilder.setContentText("Editing " + audio.getArtist() + " - " + audio.getTitle());
        mBuilder.setProgress(100, percentage, false);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void finishNotification() {
        mBuilder.setContentText("Editing complete");
        mBuilder.setProgress(0, 0, false);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void cancelNotification() {
        mBuilder.setContentText("Cancelled");
        mBuilder.setProgress(0, 0, false);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void startProgressDialog() {
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Downloading file(s)...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
    }

    public void doWork(final List<Song> data, final boolean toLower) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                audios = data;
                long start = System.currentTimeMillis();
                showProgress = true;

                //startProgressDialog();

                setupNotification();

                sendRequest(toLower);
                //sendTestRequest(audios);

            /*while (isError) {
                err = "reedit is needed";
                isError = false;
                sendRequest(failed);
                failed.clear();
            }*/
                finishNotification();
                Log.d("edit response", "" + (System.currentTimeMillis() - start));
                done();
            }
        }).start();
    }

    private void sendRequest(boolean toLower) {
        if (audios == null || audios.isEmpty()) {
            return;
        }

        Song audio;
        for (int i = 0; i < audios.size(); i++) {
            if (!isCancelled) {
                audio = audios.get(i);

                String artist;
                String title;

                artist = changeCasing(audio.getArtist(), toLower);
                title = changeCasing(audio.getTitle(), toLower);

                final VKRequest request = new VKRequest("audio.edit", VKParameters.from(
                        "audio_id", audio.getId(),
                        "owner_id", audio.getOwnerId(),
                        "artist", artist,
                        "title", title)
                );

                request.useSystemLanguage = false;
                request.attempts = 0;

                try {
                    VKJsonOperation operation = (VKJsonOperation) request.getOperation();
                    operation.start();
                    String response = operation.getResponseJson().toString();
                    Log.d("edit response", i + " - " + response);
                    //publishProgress(((i + 1) * 100) / audios.size(), audio);
                    updateNotification(((i + 1) * 100) / audios.size(), audio);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Utils.pause(300);
            }
        }

        // https://api.vk.com/method/audio.edit?aid=' + aid + '&owner_id' + owner_id + '&artist=' + artist + '&title=' + '&no_search=' + 0 + '&access_token=' + accToken
    }

    private void done() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("finish-event");
        intent.putExtra("message", "work is done");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void publishProgress(int progress, Song audio) {
        if (showProgress) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

            progressDialog.setProgress(progress);
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

    private String changeCasing(String data, boolean toLower) {
        return toLower ? data.toLowerCase() : data.toUpperCase();
    }

    public class MyBinder extends Binder {
        public EditAudiosService getService() {
            return EditAudiosService.this;
        }
    }
}

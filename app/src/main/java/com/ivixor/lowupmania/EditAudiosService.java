package com.ivixor.lowupmania;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

    public final static int NOTIFICATION_ID = 42;

    private IBinder binder = new MyBinder();

    private List<Song> audios;
    private List<Song> failed;

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
        isCancelled = true;
        //done(LoginActivity.EDIT_CANCEL);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }

    private void done() {
        Intent finishIntent = new Intent();
        if (!isCancelled) {
            finishNotification("Editing complete");
        } else {
            finishNotification("Editing cancelled");
        }
        finishIntent.setAction(LoginActivity.EDIT_FINISH);
        LocalBroadcastManager.getInstance(this).sendBroadcast(finishIntent);
    }

    private void setupNotification() {
        //Intent cancelIntent = new Intent(this, LoginActivity.class);
        Intent cancelIntent = new Intent();
        cancelIntent.setAction(LoginActivity.EDIT_CANCEL);
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
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void updateNotification(int percentage, Song audio) {
        mBuilder.setContentText(audio.getArtist() + " - " + audio.getTitle());
        mBuilder.setProgress(100, percentage, false);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void finishNotification(String status) {
        mBuilder.setContentText(status);
        mBuilder.addAction(0, null, null);
        mBuilder.setProgress(0, 0, false);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private boolean isEdited(String data, boolean toLower) {
        if (toLower) {
            return !hasUpperCase(data);
        } else {
            return !hasLowerCase(data);
        }
    }

    private boolean hasLowerCase(String data) {
        return data.matches(".*\\p{Ll}+.*");
    }

    private boolean hasUpperCase(String data) {
        return data.matches(".*\\p{Lu}+.*");
    }

    public List<Song> filterAudios(List<Song> audios, boolean toLower) {
        Log.d(TAG, "size: " + audios.size());
        List<Song> filteredAudios = new ArrayList<Song>();
        Song audio;
        for (int i = 0; i < audios.size(); i++) {
            audio = audios.get(i);
            if (!isEdited(audio.getArtist(), toLower)
                    || !isEdited(audio.getTitle(), toLower)) {
                filteredAudios.add(audio);
            }
        }

        Log.d(TAG, "size: " + filteredAudios.size());
        return filteredAudios;
    }

    public void doWork(final List<Song> data, final boolean toLower) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                audios = filterAudios(data, toLower);

                long start = System.currentTimeMillis();
                showProgress = true;

                setupNotification();

                sendRequest(toLower);

            /*while (isError) {
                err = "reedit is needed";
                isError = false;
                sendRequest(failed);
                failed.clear();
            }*/
                done();

                Log.d("edit response", "" + (System.currentTimeMillis() - start));
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

    private String changeCasing(String data, boolean toLower) {
        return toLower ? data.toLowerCase() : data.toUpperCase();
    }

    public class MyBinder extends Binder {
        public EditAudiosService getService() {
            return EditAudiosService.this;
        }
    }
}

package com.ivixor.lowupmania;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import java.util.List;


public class LoginActivity extends FragmentActivity implements LogoutDialog.NoticeDialogListener, RequestHandler.RequestHandlerListener {

    public final static String TAG = "LoginActivity";

    public final static int CANCEL_EDITING = 1;

    private static String appId = "4828248";
    private static String tokenKey = "VK_ACCESS_TOKEN";
    private static final String[] appScope = new String[] {
            VKScope.AUDIO
    };

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(appScope);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(LoginActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            Log.d("vk", "on receive token");
            newToken.saveTokenToSharedPreferences(LoginActivity.this, tokenKey);
            getAudiosList();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("vk", "accept token");
            //getAudiosList();
        }
    };

    private AudiosListFragment audiosListFragment;

    private Intent boundIntent;
    private EditAudiosService service;
    private ServiceConnection sc;
    private boolean isBound = false;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra("message");
            Log.d(TAG, msg);
        }
    };

    private BroadcastReceiver mCancelEditReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            service.cancel();
            Toast.makeText(LoginActivity.this, "on cancel click", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        audiosListFragment = new AudiosListFragment();

        VKUIHelper.onCreate(this);

        setupService();

        if (isOnline()) {
            VKSdk.initialize(sdkListener, appId); // VKSdk.initialize(sdkListener, appId, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));
            if (VKSdk.wakeUpSession()) {
                getAudiosList();
                return;
            }
            VKSdk.authorize(appScope);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);

        if (requestCode == CANCEL_EDITING) {
            if (resultCode == RESULT_OK) {
                service.cancel();
                Toast.makeText(LoginActivity.this, "on cancel click", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCancelEditReceiver);
        unbind();
        super.onDestroy();
        VKUIHelper.onDestroy(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);

        if (isOnline()) {
            if (VKSdk.isLoggedIn()) {
                getAudiosList();
                //showAudiosList();
            } else {
                VKSdk.authorize(appScope);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_lowupmania, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LogoutDialog logoutDialog = new LogoutDialog();
                logoutDialog.show(getFragmentManager(), "Logout");
                return true;
            case R.id.action_refresh:
                if (isOnline()) {
                    getAudiosList();
                    return true;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAudiosList(List<Song> audios) {
        audiosListFragment = new AudiosListFragment();
        Bundle args = new Bundle();
        args.putSerializable("audios", new DataWrapper(audios));
        audiosListFragment.setArguments(args);

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, audiosListFragment, "audios_list")
                .commit();
    }

    @Override
    public void onDialogPositiveResult(DialogFragment dialog) {
        dialog.dismiss();
        VKSdk.logout();
        finish();
    }

    @Override
    public void onRequestFinished(List<Song> audios) {
        AudiosListFragment frag =
                (AudiosListFragment) getFragmentManager().findFragmentByTag("audios_list");
        if (frag != null) {
            frag.updateData(audios);
        } else {
            showAudiosList(audios);
        }
    }

    private void getAudiosList() {
        RequestHandler handler = new RequestHandler(this);
        handler.getAudios();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void setupService() {
        boundIntent = new Intent(this, EditAudiosService.class);
        sc = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                Log.d(TAG, "connected and bound");
                EditAudiosService.MyBinder binder = (EditAudiosService.MyBinder) iBinder;
                service = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Log.d(TAG, "disconnected and unbound");
                isBound = false;
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("finish-event"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mCancelEditReceiver, null);
    }

    public void bind() {
        bindService(boundIntent, sc, BIND_AUTO_CREATE);
    }

    public void unbind() {
        if (!isBound) {
            return;
        }

        unbindService(sc);
        isBound = false;
    }

    public void editAudios(List<Song> audios, boolean toLower) {
        service.doWork(audios, toLower);
    }

    public void cancel() {
        service.cancel();
    }
}

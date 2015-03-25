package com.ivixor.lowupmania;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import java.util.List;


public class LoginActivity extends FragmentActivity implements LogoutDialog.NoticeDialogListener, RequestHandler.RequestHandlerListener {

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
            //showAudiosList();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("vk", "accept token");
            getAudiosList();
            //showAudiosList();
        }
    };

    private AudiosListFragment audiosListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        audiosListFragment = new AudiosListFragment();

        VKUIHelper.onCreate(this);

        if (isOnline()) {
            VKSdk.initialize(sdkListener, appId); // VKSdk.initialize(sdkListener, appId, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));
            if (VKSdk.wakeUpSession()) {
                getAudiosList();
                //showAudiosList();
                return;
            }
            VKSdk.authorize(appScope);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        VKUIHelper.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
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

    private void showLowupmania() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LowupmaniaFragment())
                .commit();
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
}

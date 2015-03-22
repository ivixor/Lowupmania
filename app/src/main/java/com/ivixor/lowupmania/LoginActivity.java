package com.ivixor.lowupmania;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
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


public class LoginActivity extends FragmentActivity implements LowupmaniaFragment.FooCallbacks,
                                                               LogoutDialog.NoticeDialogListener,
                                                               RequestHandler.RequestHandlerListener {

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
            showLowupmania();
        }

        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            Log.d("vk", "accept token");
            showLowupmania();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        VKUIHelper.onCreate(this);

        VKSdk.initialize(sdkListener, appId); // VKSdk.initialize(sdkListener, appId, VKAccessToken.tokenFromSharedPreferences(this, tokenKey));

        if (VKSdk.wakeUpSession()) {
            //startLowupmaniaActivity();
            showLowupmania();
            return;
        }

        VKSdk.authorize(appScope);
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

        if (VKSdk.isLoggedIn()) {
            //showLogout();
            showLowupmania();
        } else {
            VKSdk.authorize(appScope);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_lowupmania, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                LogoutDialog logoutDialog = new LogoutDialog();
                logoutDialog.show(getFragmentManager(), "Logout");

                return true;
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

    @Override
    public void foo() {

    }

    @Override
    public void onDialogPositiveResult(DialogFragment dialog) {
        dialog.dismiss();
        VKSdk.logout();
        finish();
    }

    private void startAudiosListActivity(List<Song> audios) {
        if (audios != null) {
            Intent intent = new Intent(this, AudiosListActivity.class);
            intent.putExtra("audios", new DataWrapper(audios));
            startActivity(intent);
        }
    }

    @Override
    public void onRequestFinished(List<Song> audios) {
        startAudiosListActivity(audios);
    }

    /*public static class LoginFragment extends Fragment {
        public LoginFragment() {
            super();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_login, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getView().findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.authorize(appScope, true, false);
                }
            });

            getView().findViewById(R.id.force_oauth_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.authorize(appScope, true, true);
                }
            });
        }
    }*/

    /*public static class LogoutFragment extends Fragment {
        public LogoutFragment() {
            super();
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_logout, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getView().findViewById(R.id.continue_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((LoginActivity) getActivity()).startLowupmaniaActivity();
                }
            });

            getView().findViewById(R.id.ic_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VKSdk.logout();
                    if (!VKSdk.isLoggedIn()) {
                        ((LoginActivity) getActivity()).showLogin();
                    }
                }
            });
        }
    }*/
}

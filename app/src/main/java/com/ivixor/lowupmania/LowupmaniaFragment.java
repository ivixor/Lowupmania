package com.ivixor.lowupmania;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKJsonOperation;
import com.vk.sdk.api.methods.VKApiFriends;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiCountry;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.util.VKUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class LowupmaniaFragment extends Fragment {

    public LowupmaniaFragment() {
    }

    public interface FooCallbacks {
        public void foo();
    }

    private FooCallbacks callbacks = dummyFooCallbacks;

    private static FooCallbacks dummyFooCallbacks = new FooCallbacks() {
        @Override
        public void foo() { }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lowup, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.button_get_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestHandler handler = new RequestHandler(getActivity());
                handler.getAudios();
            }
        });
    }


}

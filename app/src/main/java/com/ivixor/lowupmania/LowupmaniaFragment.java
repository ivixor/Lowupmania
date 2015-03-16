package com.ivixor.lowupmania;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.util.VKUtil;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class LowupmaniaFragment extends Fragment {

    private VKRequest.VKRequestListener requestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            List<Song> audios = null;

            AudiosJSONParser parser = new AudiosJSONParser(response.json);
            try {
                audios = parser.getAudios();
            } catch (JSONException e) {
                Log.d("json", e.getMessage());
            }

            if (audios != null) {
                Intent intent = new Intent(getActivity(), AudiosListActivity.class);
                intent.putExtra("songs", new DataWrapper(audios));
                startActivity(intent);
            }
        }

        @Override
        public void onError(VKError error) {
            Intent intent = new Intent(getActivity(), LowupmaniaActivity.class);
            intent.putExtra("result", error.toString());
            startActivityForResult(intent, 42);
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            Intent intent = new Intent(getActivity(), LowupmaniaActivity.class);
            intent.putExtra("result", request.response.toString());
            startActivityForResult(intent, 42);
        }
    };

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

        if (!(activity instanceof FooCallbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        callbacks = (FooCallbacks) activity;
    }

    public void sendRequest() {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.get_audio_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final VKRequest request = new VKRequest("audio.get", VKParameters.from(VKApiConst.FIELDS, "id,owner_id,artist,title"));
                //VKRequest request = new VKApi().users().get();
                request.secure = false;
                request.useSystemLanguage = false;
                request.executeWithListener(requestListener);
            }
        });

        getView().findViewById(R.id.low_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getView().findViewById(R.id.up_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

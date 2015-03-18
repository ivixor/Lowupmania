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
import android.widget.Toast;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.methods.VKApiFriends;
import com.vk.sdk.api.model.VKApiAudio;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.util.VKUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class LowupmaniaFragment extends Fragment implements EditAudiosAsyncTask.AsyncResponseListener {

    private EditAudiosAsyncTask editAudiosAsyncTask;

    private List<Song> audios;

    private VKRequest.VKRequestListener getAudioRequestListener = new VKRequest.VKRequestListener() {
        @Override
        public void onComplete(VKResponse response) {
            if (response.request.methodName.equals("audio.get")) {
                Log.d("get response", response.responseString);
                audios = parseJSON(response.json);
            }
        }

        @Override
        public void onError(VKError error) {
            Log.d("get response", error.errorMessage.toString());
        }

        @Override
        public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
            Log.d("get response", request.response.toString());
        }
    };

    public LowupmaniaFragment() {
    }

    @Override
    public void processFinished() {

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
        editAudiosAsyncTask = new EditAudiosAsyncTask(activity);
        editAudiosAsyncTask.delegate = this;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.button_get_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAudios();

                if (audios != null) {
                    Intent intent = new Intent(getActivity(), AudiosListActivity.class);
                    intent.putExtra("songs", new DataWrapper(audios));
                    startActivity(intent);
                }
            }
        });

        getView().findViewById(R.id.button_low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (audios != null && !audios.isEmpty()) {
                    editAudiosAsyncTask.execute(audios);
                } else {
                    Toast.makeText(getActivity(), "audio list is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        getView().findViewById(R.id.button_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private List<Song> parseJSON(JSONObject json) {
        List<Song> audios = null;

        AudiosJSONParser parser = new AudiosJSONParser(json);
        try {
            audios = parser.getAudios();
        } catch (JSONException e) {
            Log.d("json", e.getMessage());
        }

        return audios;
    }

    private void getAudios() {
        final VKRequest request = new VKRequest("audio.get", VKParameters.from(VKApiConst.FIELDS, "id,owner_id,artist,title"));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(getAudioRequestListener);
    }

    private void editAudios(List<Song> audios) {
        for (int i = 0; i < audios.size(); i++) {
            final VKRequest request = new VKRequest("audio.edit", VKParameters.from("audio_id", "350528891", "owner_id", "155554727", "artist", "Light Club".toUpperCase(), "title", "Fahkeet".toUpperCase(), "genre_id", "18"));
            request.secure = false;
            request.useSystemLanguage = false;
            request.executeWithListener(getAudioRequestListener);

        }
    }
}

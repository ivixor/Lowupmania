package com.ivixor.lowupmania;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKJsonOperation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class RequestHandler {

    public interface RequestHandlerListener {
        public void onRequestFinished(List<Song> audios);
    }

    private RequestHandlerListener listener = null;

    public RequestHandler(Context context) {
        this.listener = (RequestHandlerListener) context;
    }

    public void getAudios() {
        final List<Song> audios;

        final VKRequest request = new VKRequest("audio.get", VKParameters.from(VKApiConst.FIELDS, "id,owner_id,artist,title"));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                List<Song> audios = getAudiosFromJSON(response.json);
                listener.onRequestFinished(audios);
            }
        });
    }

    private List<Song> getAudiosFromJSON(JSONObject json) {
        List<Song> audios = null;

        AudiosJSONParser parser = new AudiosJSONParser(json);
        try {
            audios = parser.getAudiosList();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return audios;
    }
}

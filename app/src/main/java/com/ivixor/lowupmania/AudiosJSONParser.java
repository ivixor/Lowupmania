package com.ivixor.lowupmania;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AudiosJSONParser {

    private JSONObject audiosJson;

    public AudiosJSONParser(JSONObject json) {
        this.audiosJson = json;
    }

    public List<Song> getAudios() throws JSONException {

        List<Song> songs = new ArrayList<Song>();

        JSONObject response = audiosJson.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");

        Log.d("json", "" + items.length());

        for (int i = 0; i < items.length(); i++) {
            try {
                JSONObject item = items.getJSONObject(i);
                Song song = new Song(
                        item.getString("id"),
                        item.getString("owner_id"),
                        item.getString("artist"),
                        item.getString("title")
                );
                songs.add(i, song);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Log.d("json", "" + songs.size());

        return songs;
    }
}

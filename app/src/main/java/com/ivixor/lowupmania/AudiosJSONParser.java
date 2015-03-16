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

    public void parse() throws JSONException {

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
    }

    /*public List readJSONStream(String json) throws IOException {
        //JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        JsonReader reader = new JsonReader(json);
        try {
            return readMessagesArray(reader);
        } finally {
            reader.close();
        }
    }

    public List readMessagesArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    public Message readMessage(JsonReader reader) throws IOException {
        long id = -1;
        long owner = -1;
        String artist = null;
        String title = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                id = reader.nextLong();
            } else if (name.equals("owner_id")) {
                owner = reader.nextLong();
            } else if (name.equals("text")) {
                artist = reader.nextString();
            *//*} else if (name.equals("geo") && reader.peek() != JsonToken.NULL) {
                title = readDoublesArray(reader);
            } else if (name.equals("user")) {
                user = readUser(reader);
            } else {*//*
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Message(id, text, user);
        //return null;
    }

    public List readDoublesArray(JsonReader reader) throws IOException {
        List doubles = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }

    public User readUser(JsonReader reader) throws IOException {
        String username = null;
        int followersCount = -1;

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("name")) {
                username = reader.nextString();
            } else if (name.equals("followers_count")) {
                followersCount = reader.nextInt();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new User(username, followersCount);
    }*/
}

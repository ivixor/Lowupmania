package com.ivixor.lowupmania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class AudiosDataSource {

    private SQLiteDatabase database;
    private AudiosDBHelper dbHelper;
    private String[] columns = {
            AudiosContract.AudioEntry.COLUMN_NAME_AUDIO_ID,
            AudiosContract.AudioEntry.COLUMN_NAME_OWNER_ID,
            AudiosContract.AudioEntry.COLUMN_NAME_ARTIST,
            AudiosContract.AudioEntry.COLUMN_NAME_TITLE
    };

    public AudiosDataSource(Context context) {
        dbHelper = new AudiosDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Song createEntry(Song audio) {
        ContentValues values = new ContentValues();

        values.put(AudiosContract.AudioEntry.COLUMN_NAME_AUDIO_ID, audio.getId());
        values.put(AudiosContract.AudioEntry.COLUMN_NAME_OWNER_ID, audio.getOwnerId());
        values.put(AudiosContract.AudioEntry.COLUMN_NAME_ARTIST, audio.getArtist());
        values.put(AudiosContract.AudioEntry.COLUMN_NAME_TITLE, audio.getTitle());

        long insertId = database.insert(AudiosContract.AudioEntry.TABLE_NAME,
                null, values);
        Cursor cursor = database.query(AudiosContract.AudioEntry.TABLE_NAME,
                columns, AudiosContract.AudioEntry._ID + " = " + insertId, null,
                null, null, null);
        Cursor cursor1 = database.query(AudiosContract.AudioEntry.TABLE_NAME,
                columns, AudiosContract.AudioEntry._ID + " = " + 0, null,
                null, null, null);
        int i = cursor.getCount();
        cursor.moveToFirst();
        Song newAudio = cursorToAudio(cursor);
        cursor.close();

        return newAudio;
    }

    public void drop() {

    }

    public void deleteAllEntries() {
        database.delete(AudiosContract.AudioEntry.TABLE_NAME, null, null);
    }

    public void deleteEntry(Song audio) {
        String id = audio.getId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(AudiosContract.AudioEntry.TABLE_NAME, AudiosContract.AudioEntry.COLUMN_NAME_AUDIO_ID
                + " = " + id, null);
    }

    public List<Song> getAllEntries() {
        List<Song> audios = new ArrayList<Song>();

        Cursor cursor = database.query(AudiosContract.AudioEntry.TABLE_NAME,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Log.d("cursor", "cursor: " + cursor);
            Song audio = cursorToAudio(cursor);
            audios.add(audio);
            cursor.moveToNext();
        }
        cursor.close();

        return audios;
    }

    private Song cursorToAudio(Cursor cursor) {
        Song audio = new Song(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3)
        );
        return audio;
    }
}

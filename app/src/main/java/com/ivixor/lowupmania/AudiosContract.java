package com.ivixor.lowupmania;

import android.provider.BaseColumns;


public class AudiosContract {

    public static final String TEXT_TYPE = " TEXT";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AudioEntry.TABLE_NAME + " (" +
                    AudioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AudioEntry.COLUMN_NAME_AUDIO_ID + TEXT_TYPE + COMMA_SEP +
                    AudioEntry.COLUMN_NAME_OWNER_ID + TEXT_TYPE + COMMA_SEP +
                    AudioEntry.COLUMN_NAME_ARTIST + TEXT_TYPE + COMMA_SEP +
                    AudioEntry.COLUMN_NAME_TITLE + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AudioEntry.TABLE_NAME;


    public final class AudioEntry implements BaseColumns {

        public static final String TABLE_NAME = "audio";
        public static final String COLUMN_NAME_AUDIO_ID = "audio_id";
        public static final String COLUMN_NAME_OWNER_ID = "owner_id";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_TITLE = "title";

    }

}

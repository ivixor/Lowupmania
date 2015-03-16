package com.ivixor.lowupmania;


import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class DataWrapper implements Serializable {
    private List<Song> songs;

    public DataWrapper(List<Song> data) {
        this.songs = data;
    }

    public List<Song> getSongsData() {
        return songs;
    }
}

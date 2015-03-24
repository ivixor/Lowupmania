package com.ivixor.lowupmania;


import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class DataWrapper implements Serializable {
    private List<Song> audios;

    public DataWrapper(List<Song> data) {
        this.audios = data;
    }

    public List<Song> getAudiosData() {
        return audios;
    }
}

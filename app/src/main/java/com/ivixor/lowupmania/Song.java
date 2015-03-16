package com.ivixor.lowupmania;


import java.io.Serializable;

public class Song implements Serializable {
    private String id;
    private String ownerId;
    private String artist;
    private String title;

    public Song(String id, String ownerId, String artist, String title) {
        this.id = id;
        this.ownerId = ownerId;
        this.artist = artist;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }
}

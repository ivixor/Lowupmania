package com.ivixor.lowupmania;


public class Song {
    private String id = null;
    private String ownerId = null;
    private String artist = null;
    private String title = null;

    public Song() {}

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

    @Override
    public String toString() {
        return artist + " - " + title;
    }
}

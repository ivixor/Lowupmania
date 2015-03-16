package com.ivixor.lowupmania;


public class Audio {
    private String id;
    private String ownerId;
    private String artist;
    private String title;

    public Audio(String id, String ownerId, String artist, String title) {
        this.id = id;
        this.ownerId = ownerId;
        this.id = artist;
        this.id = title;
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

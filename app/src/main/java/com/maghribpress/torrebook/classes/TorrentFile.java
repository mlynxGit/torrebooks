package com.maghribpress.torrebook.classes;

public class TorrentFile {

    private String title;
    private String magnet;
    private String torrentsize;
    private String added;
    private int seeders;
    private int popularity;
    private String type;
    private String source;

    public TorrentFile() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMagnet() {
        return magnet;
    }

    public void setMagnet(String magnet) {
        this.magnet = magnet;
    }

    public String getTorrentsize() {
        return torrentsize;
    }

    public void setTorrentsize(String torrentsize) {
        this.torrentsize = torrentsize;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public int getSeeders() {
        return seeders;
    }

    public void setSeeders(int seeders) {
        this.seeders = seeders;
    }

    public int getPopularity() {
        return popularity;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

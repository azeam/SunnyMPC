package SunnyMPC;

class Track {
    private int id = 0;
    private String title = "";
    private String album = "";
    private String artist = "";
    private String MBAlbumId = "";
    private String MBArtistId = "";
    private int time = 0;
    private double duration = 0;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getArtist() {
        return artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getAlbum() {
        return album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getMBAlbumId() {
        return MBAlbumId;
    }
    public void setMBAlbumId(String MBAlbumId) {
        this.MBAlbumId = MBAlbumId;
    }
    public String getMBArtistId() {
        return MBArtistId;
    }
    public void setMBArtistId(String MBArtistId) {
        this.MBArtistId = MBArtistId;
    }
    public int getTime() {
        return time;
    }
    public void setTime(int time) {
        this.time = time;
    }
    public double getDuration() {
        return duration;
    }
    public void setDuration(double duration) {
        this.duration = duration;
    }
}
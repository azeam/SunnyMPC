package SunnyMPC;

// the track object, used to save and retrieve information about the tracks
public class Track {
    private int id = 0;
    private String title = "";
    private String album = "";
    private String artist = "";
    private String mbalbumId = "";
    private String mbartistId = "";
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
    public String getMbalbumId() {
        return mbalbumId;
    }
    public void setMbalbumId(String mbalbumId) {
        this.mbalbumId = mbalbumId;
    }
    public String getMbartistId() {
        return mbartistId;
    }
    public void setMbartistId(String mbartistId) {
        this.mbartistId = mbartistId;
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
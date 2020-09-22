package SunnyMPC;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrackBuilder {
    Track track;
    List<String> tracks = new ArrayList<String>();

    public Track getTrack() {
        return this.track;
    }

    public List<String> getTracks() {
        return tracks;
    }

    public TrackBuilder(List<String> playlist) {
        track = new Track();
        for (String row : playlist) {
            if (row.startsWith("file:")) {
                track = new Track();
            }
            else if (row.startsWith("Title:")) {
                track.setTitle(row.substring(row.indexOf(" ") + 1));
            }
            else if (row.startsWith("Album:")) {
                track.setAlbum(row.substring(row.indexOf(" ") + 1));
            }
            else if (row.startsWith("Artist:") || row.startsWith("AlbumArtist:")) {
                track.setArtist(row.substring(row.indexOf(" ") + 1));
            }
            else if (row.startsWith("MUSICBRAINZ_ALBUMID:")) {
                track.setMbalbumId(row.substring(row.indexOf(" ") + 1));
            }
            else if (row.startsWith("MUSICBRAINZ_ALBUMARTISTID:")) {
                track.setMbartistId(row.substring(row.indexOf(" ") + 1));
            }
            else if (row.startsWith("duration:")) {
                track.setDuration(Double.parseDouble(row.substring(row.indexOf(" ") + 1)));
            }
            else if (row.startsWith("Time:")) {
                track.setTime(Integer.parseInt(row.substring(row.indexOf(" ") + 1)));
            }
            else if (row.startsWith("songid:")) {
                track.setId(Integer.parseInt(row.substring(row.indexOf(" ") + 1)));
            }
            else if (row.startsWith("Id: ")) {
                track.setId(Integer.parseInt(row.substring(row.indexOf(" ") + 1)));
                ObjectMapper mapper = new ObjectMapper();
                String jsonString = "";
                try {
                    jsonString = mapper.writeValueAsString(track);
                    tracks.add(jsonString);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
	}
}
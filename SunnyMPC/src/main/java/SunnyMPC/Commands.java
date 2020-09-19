package SunnyMPC;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public List<String> getArtistList() {
        List<String> artistList = new ArrayList<String>();
        for (String row : Communicate.getList("list artist")) {
            artistList.add(row.substring(row.indexOf(" ") + 1));
        }
        return artistList;
    }

    public Object[] getData(String cmd) {
        List<String> treatedList = new ArrayList<String>();
        for (String row : Communicate.getList(cmd)) {
            treatedList.add(row.substring(row.indexOf(" ") + 1));
        }
        String[] data = treatedList.toArray(new String[0]);
        return data;
    }

    
}
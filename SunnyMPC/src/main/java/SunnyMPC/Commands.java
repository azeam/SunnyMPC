package SunnyMPC;

import java.util.List;

public class Commands {

    public List<String> getArtistList() {
        return Communicate.getList("list artist");
    }

    public Object[] getData(String cmd) {
        List<String> rawData = Communicate.getList("list file");
        String[] data = rawData.toArray(new String[0]);
        return data;
    }
}
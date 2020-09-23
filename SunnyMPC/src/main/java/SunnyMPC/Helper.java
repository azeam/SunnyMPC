package SunnyMPC;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Helper {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public String getMinutes(int time) {
        return LocalTime.MIN.plusSeconds(time).format(formatter).toString();
    }

    // the search strings need to be properly escaped
    public String escapeString(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\"); // escape backslashes
        s = s.replaceAll("\"", "\\\\\""); // escape quotes
        return '"' + s +  '"';   // put the whole thing in quotes
    }

    // remove title for each row
    public List<String> cleanupList(String cmd) {
        List<String> serverList = Communicate.sendCmd(cmd);
        List<String> list = new ArrayList<String>();
        for (String row : serverList) {
            list.add(row.substring(row.indexOf(" ") + 1));
        }
        return list;
    }
}
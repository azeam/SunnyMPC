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

    public String escapeString(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\"); // escape backslashes
        s = s.replaceAll("\"", "\\\\\""); // escape quotes
        return '"' + s +  '"';   // put the whole thing in quotes
    }

    public List<String> cleanupList(String cmd) {
        List<String> list = new ArrayList<String>();
        for (String row : Communicate.sendCmd(cmd)) {
            list.add(row.substring(row.indexOf(" ") + 1));
        }
        return list;
    }
}
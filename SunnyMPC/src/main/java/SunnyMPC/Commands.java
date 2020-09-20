package SunnyMPC;

import java.util.ArrayList;
import java.util.List;

public class Commands {

    public List<String> getList(String cmd) {
        List<String> list = new ArrayList<String>();
        for (String row : Communicate.sendCmd(cmd)) {
            list.add(row.substring(row.indexOf(" ") + 1));
        }
        return list;
    }

    public Object[] getData(String cmd) {
        List<String> treatedList = new ArrayList<String>();
        for (String row : Communicate.sendCmd(cmd)) {
            treatedList.add(row.substring(row.indexOf(" ") + 1));
        }
        String[] data = treatedList.toArray(new String[0]);
        return data;
    }

    
}
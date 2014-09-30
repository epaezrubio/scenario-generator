package poolingpeople.neo4j.scenariogenerator;

import java.util.*;

/**
 * Created by alacambra on 30.09.14.
 */
public class People {
    private String username = "U" + new Random().nextInt();
    private String uuid = UUID.randomUUID().toString();

    public String getUsername() {
        return username;
    }

    public String getUuid() {
        return uuid;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("uuid", uuid);

        return map;
    }
}

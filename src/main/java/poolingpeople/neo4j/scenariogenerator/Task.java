package poolingpeople.neo4j.scenariogenerator;

import java.util.*;

/**
 * Created by alacambra on 30.09.14.
 */
public class Task {
    private String title = "T" + new Random().nextInt();
    private String description;
    private Date startDate = new Date();
    private Date endDate = new Date();
    private String uuid = UUID.randomUUID().toString();

    public Task(){
        description = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, " +
                "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                "aliquyam erat, sed diam voluptua. At vero eos et accusam et justo " +
                "duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata " +
                "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur" +
                " sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna " +
                "aliquyam erat, sed diam voluptua. " +
                "At vero eos et accusam et justo duo dolores et ea rebum. " +
                "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("startDate", startDate.getTime());
//        map.put("endDate", endDate.getTime());
//        map.put("title", title);
//        map.put("description", description);
        map.put("uuid", uuid);

        return map;
    }
}

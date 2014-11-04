package poolingpeople.neo4j.scenariogenerator.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by: Eduardo Páez Rubio
 * Email: eduardo.paezrubio@ion2s.com
 * Date: 10/20/14
 */
public class Scenario {
    public String id = "scenario_0";
    public int user_num = 1;
    public int task_num = 1;
    public int observers_per_task = 1;
    public int linked_per_task_num = 1;
    public int roles_per_object = 1;
    public List<Long> peopleIds = new ArrayList<>();
    public List<Long> tasksIds = new ArrayList<>();

    public Scenario(String id, int user_num, int task_num, int observers_per_task, int linked_per_task_num) {
        this.id = id;
        this.user_num = user_num;
        this.task_num = task_num;
        this.observers_per_task = observers_per_task;
        this.linked_per_task_num = linked_per_task_num;
    }

    public static Map<String, Object> toMap(Integer s){

        Map<String, Object> map = new HashMap<>();
        map.put("index", s);

        return map;
    }

}

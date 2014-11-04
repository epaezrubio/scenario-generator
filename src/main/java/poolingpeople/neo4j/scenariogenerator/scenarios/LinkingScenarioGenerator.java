package poolingpeople.neo4j.scenariogenerator.scenarios;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;
import poolingpeople.neo4j.scenariogenerator.entities.People;
import poolingpeople.neo4j.scenariogenerator.entities.Scenario;
import poolingpeople.neo4j.scenariogenerator.entities.Task;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;


/**
 * Created by: Eduardo Páez Rubio
 * Email: eduardo.paezrubio@ion2s.com
 * Date: 10/27/14
 */
public class LinkingScenarioGenerator extends ScenarioGenerator {

    public void readProperties(String propertiesFile) {

        Properties prop = openPropertiesFile(propertiesFile);

        db_path = getPropertyOrException("db_path", prop);
        totalTasks = Integer.parseInt(getPropertyOrException("total-tasks", prop));
        totalPersons = Integer.parseInt(getPropertyOrException("total-people", prop));
        totalScenarios = Integer.parseInt(getPropertyOrException("total-scenarios", prop));
        linkingRatio = Integer.parseInt(getPropertyOrException("linking-ratio", prop));
        observersPerTask = Integer.parseInt(getPropertyOrException("observers-per-task", prop));

    }

    protected void writeScenarioTestParams(PrintWriter writer, Scenario scenario) {
        writer.println("[" + scenario.id + "]");
        writer.println("user_num = " + scenario.user_num);
        writer.println("task_num = " + scenario.task_num);
        writer.println("observers_per_task = " + scenario.observers_per_task);
        writer.println("linked_per_task_num = " + scenario.linked_per_task_num);
        writer.println("");
    }

    protected void writeTestHeader(PrintWriter writer, ArrayList<Scenario> scenarios) {
        writer.println("[test_suite]");
        writer.println("host = 127.0.0.1");
        writer.println("port = 7474");
        writer.println("scenarios = " + scenarios.size());
        writer.println("user_num = " + scenarios.get(0).user_num);
        writer.println("task_num = " + scenarios.get(0).task_num);
        writer.println("observers_per_task = " + scenarios.get(0).observers_per_task);
        writer.println("linked_per_task_num = " + scenarios.get(0).linked_per_task_num);
        writer.println("");

        writer.println("[db_home]");
        writer.println("test_path = " + db_path);
        writer.println("");
    }

    public void insert() {

        BatchInserter inserter = BatchInserters.inserter(this.db_path);
        Label personLabel = DynamicLabel.label("person");
        Label taskLabel = DynamicLabel.label("task");
        Label uuidLabel = DynamicLabel.label("uuid");
        inserter.createDeferredSchemaIndex(uuidLabel).on("uuid").create();
        inserter.createDeferredSchemaIndex(taskLabel).on("title").create();
        inserter.createDeferredSchemaIndex(taskLabel).on("uuid").create();
        inserter.createDeferredSchemaIndex(personLabel).on("uuid").create();

        scenarios = new ArrayList<>();
        int scenario_index = 0;
        for (int s = 1; s <= totalScenarios; s++) {
            scenario_index = scenario_index + 1;
            int sTasks = totalTasks;
            int sUsers = totalPersons;
            int sLinkingRatio = linkingRatio * s / totalScenarios;
            int sObserversPerTask = observersPerTask;
            String sId = "scenario_" + scenario_index;

            Scenario scenario = new Scenario(sId, sUsers, sTasks, sObserversPerTask, sLinkingRatio);

            scenarios.add(scenario);

            Label scenarioSpecificLabel = DynamicLabel.label(sId);
            Label scenarioLabel = DynamicLabel.label("scenario");
            inserter.createDeferredSchemaIndex(scenarioLabel).on("index").create();
            System.out.println("Creating scenario " + scenario_index);

            RelationshipType linked = DynamicRelationshipType.withName("linked");

            for (int i = 0; i < sUsers; i++) {
                People p = new People();
                long personNode = inserter.createNode(p.toMap(), personLabel, scenarioLabel, scenarioSpecificLabel, uuidLabel);
                scenario.peopleIds.add(personNode);
            }


            long scenarioNode = inserter.createNode(Scenario.toMap(s), scenarioSpecificLabel);
            long person = scenario.peopleIds.get(new Random().nextInt(scenario.peopleIds.size()));

            RelationshipType scenarioRel = DynamicRelationshipType.withName("scenario");
            inserter.createRelationship(scenarioNode, person, scenarioRel, null);

            for (int i = 0; i < sTasks; i++) {

                Task task = new Task();
                long taskNode = inserter.createNode(task.toMap(), taskLabel, scenarioSpecificLabel, uuidLabel);

                scenario.tasksIds.add(taskNode);
            }

            for (Long origin : scenario.tasksIds) {
                for (int i = 0; i < sLinkingRatio; i++) {

                    Long target = scenario.tasksIds.get(new Random().nextInt(scenario.tasksIds.size()));

                    if (target.equals(origin)) {
                        continue;
                    }

                    inserter.createRelationship(origin, target, linked, null);
                }

            }

            RelationshipType ownerRel = DynamicRelationshipType.withName("owner");
            RelationshipType assigneRel = DynamicRelationshipType.withName("assignee");
            RelationshipType observerRel = DynamicRelationshipType.withName("observer");

            for (long taskId : scenario.tasksIds) {
                long owner = scenario.peopleIds.get(new Random().nextInt(scenario.peopleIds.size()));
                long assignee = scenario.peopleIds.get(new Random().nextInt(scenario.peopleIds.size()));

                inserter.createRelationship(owner, taskId, ownerRel, null);
                inserter.createRelationship(assignee, taskId, assigneRel, null);

                for (int k = 0; k < sObserversPerTask; k++) {
                    inserter.createRelationship(scenario.peopleIds.get(
                            new Random().nextInt(scenario.peopleIds.size())), taskId, observerRel, null);
                }

            }
        }

        inserter.shutdown();

    }

}

package poolingpeople.neo4j.scenariogenerator;

import org.apache.commons.cli.*;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by alacambra on 30.09.14.
 */
public class Executor {

    private String path;
    private int totalTasks;
    private int totalPersons;
    private int linkingRatio;
    private int observersPerTask;


    public static void main(String[] args) throws ParseException {

        CommandLine commandLine;
        Option optionPropertiesFile = OptionBuilder
                .withArgName("properties").hasArg()
                .withDescription("Path to the properties file")
                .isRequired().create("p");

        Options options = new Options();
        options.addOption(optionPropertiesFile);
        CommandLineParser parser = new GnuParser();

        Executor executor = new Executor();
        commandLine = parser.parse(options, args);
//        System.out.println(commandLine.getOptionValue("p"));
        executor.readProperties(commandLine.getOptionValue("p"));

        executor.insert();
    }

    public void readProperties(String propertiesFile){

        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try{
            prop.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        path = getPropertyOrException("path", prop);
        totalTasks = Integer.parseInt(getPropertyOrException("total-tasks", prop));
        totalPersons = Integer.parseInt(getPropertyOrException("total-people", prop));
        linkingRatio = Integer.parseInt(getPropertyOrException("linking-ratio", prop));
        observersPerTask = Integer.parseInt(getPropertyOrException("observers-per-task", prop));
    }

    private String getPropertyOrException(String property, Properties prop){

        String p = prop.getProperty(property);

        if(p == null){
            throw new RuntimeException(property + " cannot be null:" + prop);
        }

        return p;
    }

    public void insert() {

        BatchInserter inserter = BatchInserters.inserter(this.path);
        Label personLabel = DynamicLabel.label("person");
        Label taskLabel = DynamicLabel.label("task");
        Label uuidLabel = DynamicLabel.label("uuid");
        inserter.createDeferredSchemaIndex(uuidLabel).on("uuid").create();
        inserter.createDeferredSchemaIndex(taskLabel).on("title").create();
        inserter.createDeferredSchemaIndex(taskLabel).on("uuid").create();
        inserter.createDeferredSchemaIndex(personLabel).on("uuid").create();
        List<Long> tasksIds = new ArrayList<>();
        List<Long> peopleIds = new ArrayList<>();

        RelationshipType linked = DynamicRelationshipType.withName("linked");

        for(int i = 0, total = totalTasks, percent = 0; i<total; i++) {

            if(i%(Math.ceil(total/100))==0){
                percent++;
                System.out.println("tasks done " + percent + "%");
            }

            Task t = new Task();
            long taskNode = inserter.createNode(t.toMap(), taskLabel, uuidLabel);

            tasksIds.add(taskNode);
        }

        int i = 0, percent=0;
        for(Long origin : tasksIds){
            for(int j = 0; j<linkingRatio; j++) {

                Long target = tasksIds.get(new Random().nextInt(tasksIds.size()));

                if (target == origin){
                    continue;
                }

                inserter.createRelationship(origin, target, linked, null);
            }

            if(i%(Math.ceil(tasksIds.size()/100))==0){
                percent++;
                System.out.println("links done " + percent + "%");
            }
            i++;
        }

        percent=0;
        for(int j = 0; j<totalPersons; j++) {

            if(j%(Math.ceil(totalPersons/100))==0){
                percent++;
                System.out.println("persons done " + percent + "%");
            }

            People p = new People();
            long personNode = inserter.createNode(p.toMap(), personLabel, uuidLabel);
            peopleIds.add(personNode);
        }

        RelationshipType ownerRel = DynamicRelationshipType.withName("owner");
        RelationshipType assigneRel = DynamicRelationshipType.withName("assignee");
        RelationshipType observerRel = DynamicRelationshipType.withName("observer");

        i = 0;
        percent = 0;
        for(long taskId : tasksIds){
            long owner = peopleIds.get(new Random().nextInt(peopleIds.size()));
            long assignee = peopleIds.get(new Random().nextInt(peopleIds.size()));

            inserter.createRelationship(owner, taskId, ownerRel, null);
            inserter.createRelationship(assignee, taskId, assigneRel, null);

            for(int k = 0; k < observersPerTask; k++){
                inserter.createRelationship(peopleIds.get(
                        new Random().nextInt(peopleIds.size())), taskId, observerRel, null);
            }

            if(i%(tasksIds.size()/100)==0){
                percent++;
                System.out.println("rolls done " + percent + "%");
            }
            i++;

        }

        inserter.shutdown();

    }
}


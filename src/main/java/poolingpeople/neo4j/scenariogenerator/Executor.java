package poolingpeople.neo4j.scenariogenerator;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserters;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by alacambra on 30.09.14.
 */
public class Executor {

    public static void main(String[] args){
        new Executor().insert();
    }

    public void insert() {

        BatchInserter inserter = BatchInserters.inserter("/home/alacambra/neo4j-community-2.1.2/data/graph.db");
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

        for(int i = 0, total = 1000000, percent = 0; i<total; i++) {

            if(i%(total/100)==0){
                percent++;
                System.out.println("done " + percent + "%");
            }

            Task t = new Task();
            long taskNode = inserter.createNode(t.toMap(), taskLabel, uuidLabel);

            tasksIds.add(taskNode);
        }

        int i = 0, percent=0;
        for(Long origin : tasksIds){
            for(int j = 0; j<10; j++) {

                Long target = tasksIds.get(new Random().nextInt(tasksIds.size()));

                if (target == origin){
                    continue;
                }

                inserter.createRelationship(origin, target, linked, null);
            }

            if(i%(tasksIds.size()/100)==0){
                percent++;
                System.out.println("links done " + percent + "%");
            }
            i++;
        }

        i = 0;
        percent=0;
        for(int j = 0, total = 10000; j<total; j++) {

            if(i%(total/100)==0){
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

            for(int k = 0; k<5; k++){
                inserter.createRelationship(new Random().nextInt(peopleIds.size()), taskId, observerRel, null);
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


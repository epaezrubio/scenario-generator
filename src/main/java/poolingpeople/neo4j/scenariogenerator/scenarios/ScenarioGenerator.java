package poolingpeople.neo4j.scenariogenerator.scenarios;

import poolingpeople.neo4j.scenariogenerator.entities.Scenario;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by: Eduardo Páez Rubio
 * Email: eduardo.paezrubio@ion2s.com
 * Date: 10/27/14
 */
public abstract class ScenarioGenerator {

    protected String db_path;
    protected int totalPersons;
    protected int totalScenarios;
    protected int totalTasks;
    protected int linkingRatio;
    protected int observersPerTask;
    protected ArrayList<Scenario> scenarios;

    protected abstract void writeTestHeader(PrintWriter writer, ArrayList<Scenario> scenarios);

    protected abstract void writeScenarioTestParams(PrintWriter writer, Scenario scenario);

    public abstract void readProperties(String propertiesFile);

    public abstract void insert();

    protected Properties openPropertiesFile(String propertiesFile) {
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(propertiesFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            prop.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return prop;
    }

    public void saveConfig() {
        try {
            PrintWriter writer = new PrintWriter("config.ini", "UTF-8");
            writeTestHeader(writer, scenarios);

            for (Scenario scenario : scenarios) {
                writeScenarioTestParams(writer, scenario);
            }

            writer.close();

        } catch (Exception e) {
            System.err.println("Could not create config file");
            System.err.println(e.getMessage());
        }
    }

    protected String getPropertyOrException(String property, Properties prop) {

        String p = prop.getProperty(property);

        if (p == null) {
            throw new RuntimeException(property + " cannot be null:" + prop);
        }

        return p;
    }

}

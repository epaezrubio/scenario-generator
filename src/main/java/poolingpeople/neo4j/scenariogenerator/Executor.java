package poolingpeople.neo4j.scenariogenerator;

import org.apache.commons.cli.*;
import poolingpeople.neo4j.scenariogenerator.scenarios.BlacklistScenarioGenerator;
import poolingpeople.neo4j.scenariogenerator.scenarios.LinkingScenarioGenerator;
import poolingpeople.neo4j.scenariogenerator.scenarios.OwnerScenarioGenerator;
import poolingpeople.neo4j.scenariogenerator.scenarios.ScenarioGenerator;

import java.util.Random;

/**
 * Created by alacambra on 30.09.14.
 */
public class Executor {

    public static void main(String[] args) throws ParseException {

        CommandLine commandLine;
        Option optionPropertiesFile = OptionBuilder
                .withArgName("properties").hasArg()
                .withDescription("Path to the properties file")
                .isRequired().create("p");

        Options options = new Options();
        options.addOption(optionPropertiesFile);

        CommandLineParser parser = new GnuParser();
        commandLine = parser.parse(options, args);

        createScenario(new OwnerScenarioGenerator(), commandLine.getOptionValue("p"));


    }


    private static void createScenario(ScenarioGenerator s, String propertiesFile) {

        s.readProperties(propertiesFile);

        try {
            s.insert();
        } catch (IllegalStateException e) {
            System.err.println("Error when trying to write the file. Is the database correctly shut down or are files corrupted?");
            return;
        } catch (RuntimeException e) {
            System.err.println("Error when trying to write the file. Is the database running and have permissions to write the output directory?");
            return;
        }

        s.saveConfig();

    }


}


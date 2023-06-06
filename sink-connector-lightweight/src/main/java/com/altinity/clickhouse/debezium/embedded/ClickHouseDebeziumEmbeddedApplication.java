package com.altinity.clickhouse.debezium.embedded;

import com.altinity.clickhouse.debezium.embedded.cdc.DebeziumChangeEventCapture;
import com.altinity.clickhouse.debezium.embedded.common.PropertiesHelper;
import com.altinity.clickhouse.debezium.embedded.config.ConfigLoader;
import com.altinity.clickhouse.debezium.embedded.config.ConfigurationService;
import com.altinity.clickhouse.debezium.embedded.ddl.parser.DDLParserService;
import com.altinity.clickhouse.debezium.embedded.parser.DebeziumRecordParserService;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ClickHouseDebeziumEmbeddedApplication {

    private static final Logger log = LoggerFactory.getLogger(ClickHouseDebeziumEmbeddedApplication.class);

    /**
     * Main Entry for the application
     * @param args arguments
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        //BasicConfigurator.configure();
        System.setProperty("log4j.configurationFile", "resources/log4j2.xml");

        org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout("%r %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %p %c %x - %m%n")));

        String loggingLevel = System.getenv("LOGGING_LEVEL");
        if(loggingLevel != null) {
            // If the user passes a wrong level, it defaults to DEBUG
            LogManager.getRootLogger().setLevel(Level.toLevel(loggingLevel));
        } else {
            LogManager.getRootLogger().setLevel(Level.INFO);
        }
        Injector injector = Guice.createInjector(new AppInjector());

        Properties props = new Properties();
        if(args.length > 0) {
            log.info(String.format("****** CONFIGURATION FILE: %s ********", args[0]));

            try {
                Properties defaultProperties = PropertiesHelper.getProperties("config.properties");

                props.putAll(defaultProperties);
                Properties fileProps = new ConfigLoader().loadFromFile(args[0]);
                props.putAll(fileProps);
            } catch(Exception e) {
                log.error("Error parsing configuration file, USAGE: java -jar <jar_file> <yaml_config_file>");
                System.exit(-1);
            }
        } else {
            props = injector.getInstance(ConfigurationService.class).parse();
            log.error("Error parsing configuration file, USAGE: java -jar <jar_file> <yaml_config_file>");
            System.exit(-1);
        }

        ClickHouseDebeziumEmbeddedApplication csg = new ClickHouseDebeziumEmbeddedApplication();
        csg.start(injector.getInstance(DebeziumRecordParserService.class),
                injector.getInstance(ConfigurationService.class),
                injector.getInstance(DDLParserService.class), props);

    }


    public void start(DebeziumRecordParserService recordParserService,
                      ConfigurationService configurationService,
                      DDLParserService ddlParserService, Properties props) throws Exception {
        // Define the configuration for the Debezium Engine with MySQL connector...
       // log.debug("Loading properties");
        //final Properties props = new ConfigLoader().load();


        DebeziumChangeEventCapture eventCapture = new DebeziumChangeEventCapture();
        eventCapture.setup(props, recordParserService, ddlParserService);

    }

    public void start(DebeziumRecordParserService recordParserService,
                      Properties props,
                      DDLParserService ddlParserService) throws Exception {
        // Define the configuration for the Debezium Engine with MySQL connector...
        log.debug("Loading properties");

        DebeziumChangeEventCapture eventCapture = new DebeziumChangeEventCapture();
        eventCapture.setup(props, recordParserService, ddlParserService);

    }
}


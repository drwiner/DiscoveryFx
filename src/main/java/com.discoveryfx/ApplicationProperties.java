package com.discoveryfx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ApplicationProperties {

    public static final String propFileName = "application.properties";

    private static final Logger logger = LoggerFactory.getLogger(ApplicationProperties.class);

    private static Map<Property, String> properties = new HashMap<>();

    public enum Property {
        EMBEDDING_DIR,
        CLUSTER_REPORT,
        DATA_PACKAGE
    }


    static {
        InputStream iStream = null;

        try {
            Properties prop = new Properties();

            try {
                URL resource = ApplicationProperties.class.getClassLoader().getResource(propFileName);
                logger.info(resource.toString());
                String file = resource.getFile();
                logger.info(file.toLowerCase());
                iStream = new FileInputStream(file);
                prop.load(iStream);
            } catch (FileNotFoundException ex) {
                logger.error("Couldn't find property file: " + propFileName, ex);
            }

            properties.put(Property.EMBEDDING_DIR, prop.getProperty("embedding_dir"));
            properties.put(Property.CLUSTER_REPORT, prop.getProperty("cluster_report"));
            properties.put(Property.DATA_PACKAGE, prop.getProperty("data_package_intents"));


        } catch (Exception e) {
            logger.error("Exception reading " + propFileName, e);
        } finally {
            try {
                if(iStream != null){
                    iStream.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                logger.error("Couldn't close stream", e);
            }
        }
    }

    public static String getProperty(Property propertyName){
        if (!properties.containsKey(propertyName)){
            throw new IllegalArgumentException("Somehow asked for a property enum that doesn't exist");
        }

        return properties.get(propertyName);
    }


}
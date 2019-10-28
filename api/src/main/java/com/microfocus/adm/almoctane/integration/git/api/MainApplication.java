/*
Copyright 2019 EntIT Software LLC, a Micro Focus company, L.P.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.microfocus.adm.almoctane.integration.git.api;

import com.microfocus.adm.almoctane.integration.git.config.CommonUtils;
import com.microfocus.adm.almoctane.integration.git.config.Factory;
import com.microfocus.adm.almoctane.integration.git.config.OctanePool;
import com.microfocus.adm.almoctane.integration.git.config.OctanePoolException;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Properties;

@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {

    static {
        Properties configurationFileProperties;

        try {
            configurationFileProperties = CommonUtils.loadPropertiesFromConfFolder("configuration.properties");
            initLogs(configurationFileProperties);
            Factory.getInstance();
            OctanePool.getPool();
        } catch (IOException | OctanePoolException e) {
            throw new RuntimeException("Please provide a correct configuration file in the conf folder!", e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MainApplication.class);
    }

    private static void initLogs(Properties configurationFileProperties) {
        String pathToLogs = configurationFileProperties.getProperty("logs.location");

        //logs.location not set in the configuration file -> defaul
        if (pathToLogs == null) {
            setDefaultLogsLocation();
            return;
        }

        pathToLogs = pathToLogs.trim().replace("\\", "/");
        File logsFolder = new File(pathToLogs);


        if (!logsFolder.exists() || !logsFolder.isDirectory()) {
            System.out.println("The path provided for the log files in the configuration file does not " +
                    "represent a path to an existing folder. The logs will be placed in the default location.");
            setDefaultLogsLocation();
            return;
        }

        setLog4jFileLocation(pathToLogs);
        LoggerFactory.getLogger(MainApplication.class)
                .info(String.format("Logs location: %s/octane_utility_logs folder", pathToLogs));

    }

    private static void setLog4jFileLocation(String pathToLogs) {
        try {
            Properties logsProperty = CommonUtils.loadPropertyFile(
                    new File(MainApplication.class.getResource("/log4j.properties").toURI()));

            //make sure path ends with "/"
            pathToLogs = pathToLogs.endsWith("/") ? pathToLogs : pathToLogs + "/";

            String logFileLocation = logsProperty.getProperty("log4j.appender.file.File");

            if (logFileLocation != null) {
                logFileLocation = logFileLocation.replaceAll("\\$\\{(.*)}/", pathToLogs);
            } else {
                logFileLocation = pathToLogs + "octane_utility_logs/octane_git_integration_logs.log";
            }
            logsProperty.setProperty("log4j.appender.file.File", logFileLocation);
            PropertyConfigurator.configure(logsProperty);
        } catch (IOException | URISyntaxException e) {
            System.err.println("Could not read the logs property file");
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

    }

    private static void setDefaultLogsLocation() {
        try {
            File jarFile = new File(MainApplication.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            String logFolderPath = jarFile.getParentFile().getParentFile().toPath().toString();

            setLog4jFileLocation(logFolderPath);

            LoggerFactory.getLogger(MainApplication.class)
                    .info(String.format("Installation folder: %s. The logs can be found in  the octane_utility_logs folder", logFolderPath));
        } catch (URISyntaxException e) {
            System.out.println("Default logs folder could not be initialized!");
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
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
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class MainApplication extends SpringBootServletInitializer {

    static {
        Properties configurationFileProperties;

        try {
            configurationFileProperties = CommonUtils.loadProperties("configuration.properties");
        } catch (IOException e) {
            throw new RuntimeException("Please provide a configuration file in the conf folder!");
        }

        initLogs(configurationFileProperties);
    }

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MainApplication.class);
    }

    private static void initLogs(Properties configurationFileProperties) {
        String logsLocation = configurationFileProperties.getProperty("logs.location");

        if (logsLocation != null) {
            System.setProperty("git-integration-for-octane-log-folder", logsLocation);
            LoggerFactory.getLogger(MainApplication.class).info(String.format("Logs location: %s\\octane_utility_logs folder", logsLocation));
        } else {

            String installationFolder = new File(MainApplication.class
                    .getProtectionDomain().getCodeSource().getLocation().getPath())
                    .getParentFile()
                    .getParent();

            System.setProperty("git-integration-for-octane-log-folder", "logs");
            LoggerFactory.getLogger(MainApplication.class).info(String.format("Installation folder: %s. The logs can be found in  the octane_utility_logs folder", installationFolder));
        }
    }
}
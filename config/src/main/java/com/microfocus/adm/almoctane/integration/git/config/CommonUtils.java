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

package com.microfocus.adm.almoctane.integration.git.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Methods which can be useful in more than one class.
 */
public class CommonUtils {

    /**
     * Loads the configuration file properties.
     *
     * @param propertyFileName - The name of the configuration file.
     * @return - The properties.
     * @throws IOException - In case the configuration file is named differently or does not exist.
     */
    public static Properties loadPropertiesFromConfFolder(String propertyFileName) throws IOException {

        try {
            File jarFile = new File(CommonUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File configurationFile = jarFile.getParentFile()
                    .getParentFile()
                    .getParentFile()
                    .toPath().resolve("conf").resolve(propertyFileName)
                    .toFile();
            return loadPropertyFile(configurationFile);
        } catch (URISyntaxException e) {
            throw new RuntimeException("Error getting the configuration file", e);
        }
    }


    public static Properties loadPropertyFile(File file) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return properties;
    }
}

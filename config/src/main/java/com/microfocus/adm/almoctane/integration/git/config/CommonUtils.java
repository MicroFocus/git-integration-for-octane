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
import java.io.IOException;
import java.io.InputStream;
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
    public static Properties loadProperties(String propertyFileName) throws IOException {
        Properties properties = new Properties();

        File file = new File(Factory.class.getProtectionDomain().getCodeSource().getLocation().getPath());

        String inputFilePath = String.format("%s%s%s%s%s",
                file.getParentFile().getParentFile().getParent(),
                File.separator, "conf",
                File.separator,
                propertyFileName);

        InputStream input = new FileInputStream(inputFilePath);
        properties.load(input);

        return properties;
    }
}

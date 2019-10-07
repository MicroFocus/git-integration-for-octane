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

import java.util.Arrays;

/**
 * Exception used when the factory encounters a problem
 */
public class FactoryException extends RuntimeException {
    /**
     * Creates a FactoryException when there are missing fields in the property file that are required for the creation
     * of a certain type of repository
     *
     * @param repoHost - the name of the repository host
     * @param values   - the fields which cannot be empty
     */
    public FactoryException(String repoHost, String... values) {
        super("Since the " + PropertiesFileKeys.REPOSITORY_HOST + " is " + repoHost + " the values for \'" + Arrays.toString(values) + "\' cannot be empty");
    }
}

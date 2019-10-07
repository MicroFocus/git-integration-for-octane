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

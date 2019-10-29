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

import com.hpe.adm.nga.sdk.Octane;
import com.hpe.adm.nga.sdk.authentication.SimpleClientAuthentication;
import com.hpe.adm.nga.sdk.authentication.SimpleUserAuthentication;
import com.hpe.adm.nga.sdk.exception.OctanePartialException;
import com.hpe.adm.nga.sdk.network.google.GoogleHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This is a pool of different Octane connections.
 */
public class OctanePool {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctanePool.class);

    private static Map<Long, OctaneUser> octaneSharedSpaceAndUsersMap;

    private static final String propertyFileName = "configuration.properties";
    private static Properties properties;
    private static OctanePool octanePool;

    private Map<OctaneObject, Octane> octaneObjects = new HashMap<>();

    /**
     * Returns the Octane connection for the shared space, workspace and URL given.
     * If the connection does not exist in this pool, it will be added.
     *
     * @param sharedSpace - The Octane shared space.
     * @param workspace   - The Octane workspace.
     * @param url         - The Octane URL.
     * @return - A connection to the Octane URL.
     */
    public Octane getOctane(long sharedSpace, long workspace, String url) {
        OctaneObject octaneObject = new OctaneObject(sharedSpace, workspace);

        verifyURL(url);

        addOctane(sharedSpace, workspace, octaneObject);

        return octaneObjects.get(octaneObject);
    }

    /**
     * Returns a GoogleHttpClient for Octane.
     *
     * @param sharedSpace - The Octane shared space.
     * @param url         - The Octane URL.
     * @return - The http client.
     */
    public GoogleHttpClient getOctaneHttpClient(long sharedSpace, String url) {
        verifyURL(url);

        GoogleHttpClient googleHttpClient = new GoogleHttpClient(properties.getProperty(PropertiesFileKeys.OCTANE_SERVER).trim());
        googleHttpClient.authenticate(new SimpleUserAuthentication(
                octaneSharedSpaceAndUsersMap.get(sharedSpace).getUser(),
                octaneSharedSpaceAndUsersMap.get(sharedSpace).getPassword(), "HPE_REST_API_TECH_PREVIEW"));

        return googleHttpClient;
    }

    /**
     * Returns the OctanePool object. In case the pool was not initialized, it is done here.
     *
     * @return - The OctanePool object.
     */
    public static OctanePool getPool() {
        if (properties == null) {
            octanePool = new OctanePool();
        }
        return octanePool;
    }


    /**
     * Adds a new Octane connection to the pool.
     *
     * @param sharedSpace  - The Octane shared space.
     * @param workspace    - The Octane workspace.
     * @param octaneObject - The OctaneObject which will be added to the octaneObjects list, to keep track of
     *                     the Octane connections
     */
    private synchronized void addOctane(long sharedSpace, long workspace, OctaneObject octaneObject) {
        if (isInPool(octaneObject))
            return;
        LOGGER.info(String.format("Adding new Octane to the OctanePool. Shared space %s, workspace %s", sharedSpace, workspace));
        if (octaneSharedSpaceAndUsersMap.get(sharedSpace) == null) {
            throw new OctanePoolException("No credentials are set for the shared space " + sharedSpace + " and a request " +
                    "was made from this shared space!");
        }
        try {
            Octane octane = new Octane.Builder(
                    new SimpleClientAuthentication(
                            octaneSharedSpaceAndUsersMap.get(sharedSpace).getUser(),
                            octaneSharedSpaceAndUsersMap.get(sharedSpace).getPassword(), "HPE_REST_API_TECH_PREVIEW"))
                    .Server(properties.getProperty(PropertiesFileKeys.OCTANE_SERVER).trim())
                    .sharedSpace(sharedSpace)
                    .workSpace(workspace)
                    .build();
            octaneObjects.put(octaneObject, octane);
            LOGGER.info(String.format("Successfully added octane to OctanePool. Shared space %s, workspace %s", sharedSpace, workspace));
        } catch (OctanePartialException e) {
            throw new OctanePoolException("Error building octane. Shared space:" + sharedSpace + ". Workspace: " + workspace + ". " +
                    "Error message: " + e.getErrorModels().iterator().next().getValue("description").getValue().toString(),
                    e);
        } catch (RuntimeException e) {
            throw new OctanePoolException("Error building octane. Shared space: " + sharedSpace + ". Workspace: " + workspace + ". " +
                    "Error message: " + e.getMessage(),
                    e);
        }


    }

    /**
     * Checks if the url given is the same as the one in the configuration file.
     *
     * @param url - The url of Octane which will be checked.
     */
    private void verifyURL(String url) {
        String serverURL = properties.getProperty(PropertiesFileKeys.OCTANE_SERVER).trim();

        if (!url.equals(serverURL)) {
            throw new OctanePoolException(String.format("Error while verifying octane URL." +
                    "\n\t\tConfig file url: %s\n\t\tURL from where the request was fired: %s", serverURL, url));
        }
    }

    /**
     * Checks if the Octane connection has already been added to the pool.
     *
     * @param octaneObject - The Octane object.
     * @return - true if the connection has already been added to the pool.
     * - false otherwise.
     */
    private boolean isInPool(OctaneObject octaneObject) {
        return octaneObjects.containsKey(octaneObject);
    }

    /**
     * Initialized the Octane pool by loading the properties from the configuration file.
     */
    private OctanePool() {
        try {
            properties = CommonUtils.loadPropertiesFromConfFolder(propertyFileName);

            loadOctaneSharedSpaceAndUsersMap();
        } catch (IOException e) {
            throw new OctanePoolException(String.format("Exception occurred during pool initialization. Exception message: %s Stack trace %s", e.getMessage(), Arrays.toString(e.getStackTrace())));
        }
    }

    /**
     * Maps the shared spaces with credentials. The shared spaces ids and credentials(user/API key, password/secret)
     * are taken from the configuration file.
     */
    private void loadOctaneSharedSpaceAndUsersMap() {
        octaneSharedSpaceAndUsersMap = new HashMap<>();

        String[] sharedSpaces = properties.getProperty(PropertiesFileKeys.OCTANE_SHARED_SPACE).split(",");
        String[] users = properties.getProperty(PropertiesFileKeys.OCTANE_USER).split(",");
        String[] passwords = properties.getProperty(PropertiesFileKeys.OCTANE_PASSWORD).split(",");

        if (sharedSpaces.length != users.length || sharedSpaces.length != passwords.length)
            throw new OctanePoolException(
                    "There needs to be exactly one user and password pair for every shared space. " +
                            "Number of shared spaces: " + sharedSpaces.length + ". " +
                            "Number of users :" + users.length + ". " +
                            "Number of passwords :" + passwords.length);

        for (int i = 0; i < sharedSpaces.length; i++) {
            try {
                octaneSharedSpaceAndUsersMap.put(Long.valueOf(sharedSpaces[i].trim()), new OctaneUser(users[i].trim(), passwords[i].trim()));
            } catch (NumberFormatException e) {
                throw new OctanePoolException("Please provide a valid shared space number in the configuration file.", e);
            }
        }
    }
}

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

import com.google.api.client.http.HttpTransport;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.BitbucketRepositoryConnectionAdapter;
import com.microfocus.adm.almoctane.integration.git.common.OctaneService;
import com.microfocus.adm.almoctane.integration.git.common.RepositoryConnectionAdapter;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneUDF;
import com.microfocus.adm.almoctane.integration.git.config.httpclient.HttpTransporter;
import com.microfocus.adm.almoctane.integration.git.octaneendpoint.OctaneRequestService;
import com.microfocus.adm.almoctane.integration.git.octaneendpoint.OctaneServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Creates connections to Octane and different repositories.
 */
public class Factory {
    private static final String propertyFileName = "configuration.properties";
    private Map<OctaneUDF.Type, OctaneUDF> userDefinedFields;

    private static Factory factory;

    private static Properties properties;

    private static RepositoryConnectionAdapter implementation;

    /**
     * Creates a new instance o this factory.
     * The properties from the configuration file are stored in the 'properties'.
     *
     * @throws IOException - In case the configuration file is named differently or does not exist.
     */
    private Factory() throws IOException {
        properties = CommonUtils.loadProperties(propertyFileName);

        userDefinedFields = new HashMap<>();

        userDefinedFields.put(OctaneUDF.Type.PULL_REQUEST, new OctaneUDF(properties.getProperty(PropertiesFileKeys.PULL_REQUESTS_INFORMATION_UDF_NAME).trim(),
                properties.getProperty(PropertiesFileKeys.PULL_REQUESTS_INFORMATION_UDF_LABEL).trim(), OctaneUDF.Type.PULL_REQUEST));

        userDefinedFields.put(OctaneUDF.Type.BRANCH, new OctaneUDF(properties.getProperty(PropertiesFileKeys.BRANCH_INFORMATION_UDF_NAME).trim(),
                properties.getProperty(PropertiesFileKeys.BRANCH_INFORMATION_UDF_LABEL).trim(), OctaneUDF.Type.BRANCH));

        String repoHost, server, access;
        repoHost = properties.getProperty(PropertiesFileKeys.REPOSITORY_HOST).trim();

        switch (repoHost) {
            case PropertiesFileKeys.BITBUCKET_SERVER:
                String urlKey = repoHost + PropertiesFileKeys._URL;
                String accessKey = repoHost + PropertiesFileKeys._ACCESS;
                server = properties.getProperty(urlKey).trim();
                access = properties.getProperty(accessKey).trim();
                if (server.equals("") || access.equals(""))
                    throw new FactoryException(repoHost, urlKey, accessKey);
                HttpTransport httpTransport = HttpTransporter.getInstance().getHttpTransport();
                implementation = new BitbucketRepositoryConnectionAdapter(server, access, httpTransport);
                break;
            default:
                throw new IOException("Configuration file contains an unknown " + PropertiesFileKeys.REPOSITORY_HOST + " name. Actual value: " + repoHost);
        }

        setProxy();
    }

    /**
     * Returns a Factory object.
     * The initialization is done here in case the object was not already initialized.
     *
     * @return - A factory object.
     * @throws IOException - In case the configuration file is named differently or does not exist.
     */
    public static Factory getInstance() throws IOException {
        if (factory == null) {
            factory = new Factory();
        }
        return factory;
    }

    /**
     * @return - Returns a connection to a repository, based on the configuration file.
     */
    public RepositoryConnectionAdapter getImplementation() {
        return implementation;
    }

    /**
     * Returns an OctaneService.
     *
     * @param id          - The id of the entity for which the user made a request. (i.e. defect with id 1001)
     * @param sharedSpace - The shared space id.
     * @param workspace   - The workspace id.
     * @param url         - The URL from where the request was fired.
     * @return - An OctaneService.
     */
    public OctaneService getOctaneService(String id, long sharedSpace, long workspace, String url) {
        OctaneRequestService octaneRequestService = new OctaneRequestService(OctanePool.getPool().getOctane(sharedSpace, workspace, url),
                OctanePool.getPool().getOctaneHttpClient(sharedSpace, url), url, sharedSpace, workspace);

        return new OctaneServiceImpl(id, octaneRequestService, userDefinedFields);
    }

    /**
     * Sets a system proxy. The proxy details are taken from the configuration file.
     */
    private static void setProxy() {
        if (properties.containsKey(PropertiesFileKeys.PROXY_HOST) && properties.containsKey(PropertiesFileKeys.PROXY_PORT)) {
            System.setProperty("http.proxyHost", properties.getProperty(PropertiesFileKeys.PROXY_HOST).trim());
            System.setProperty("http.proxyPort", properties.getProperty(PropertiesFileKeys.PROXY_PORT).trim());

            System.setProperty("https.proxyHost", properties.getProperty(PropertiesFileKeys.PROXY_HOST).trim());
            System.setProperty("https.proxyPort", properties.getProperty(PropertiesFileKeys.PROXY_PORT).trim());
        }
    }
}

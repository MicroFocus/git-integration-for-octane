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

package com.microfocus.adm.almoctane.integration.git.config.httpclient;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;

/**
 *  Since there should be a single instance of HttpTransport in the whole project, this class provides that instance.
 */
public class HttpTransporter {
    private static HttpTransporter instance;
    private HttpTransport httpTransport;

    /**
     * Creates the HttpTransport instance
     */
    private HttpTransporter() {
        httpTransport = new ApacheHttpTransport();
    }

    /**
     * Ensures only one instance of the transporter can be made
     * @return - transporter instance which contains the single HttpTransport
     */
    public static HttpTransporter getInstance() {
        if (instance == null)
            instance = new HttpTransporter();
        return instance;
    }

    /**
     *
     * @return - HttpTransport instance
     */
    public HttpTransport getHttpTransport() {
        return httpTransport;
    }
}

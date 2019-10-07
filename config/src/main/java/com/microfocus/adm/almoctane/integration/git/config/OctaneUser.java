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

/**
 * A mapper for users and passwords (API key, secrets).
 */
public class OctaneUser {
    private String user;
    private String password;

    /**
     * User and password pair or api key and secret pair
     *
     * @param user     - the username or api key with which the connection to Octane is made
     * @param password - the password or the secret
     */
    public OctaneUser(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * @return - The user.
     */
    public String getUser() {
        return this.user;
    }

    /**
     * @return - The password.
     */
    public String getPassword() {
        return this.password;
    }
}
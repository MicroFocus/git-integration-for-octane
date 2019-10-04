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

package com.microfocus.adm.almoctane.integration.git.common.exceptions;


/**
 * Exception used when there is a problem with the URL of the repository
 */
public class InvalidUrlRepositoryException extends RepositoryException {

    /**
     * @param message      - message of the exception
     * @param cause        - cause of the exception
     * @param repoHostName - the name of the repository host
     * @param actualUrl    - the URL that was used
     * @param repoHostUrl  -  the URL of the repository
     */
    public InvalidUrlRepositoryException(String message, Throwable cause, String repoHostName, String actualUrl, String repoHostUrl) {
        super(message, cause, repoHostName, repoHostUrl);
        lineList.add(0, "Could not use the " + actualUrl + " url");
    }

}

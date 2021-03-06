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
 * Exception used when there is a problem related to the repository
 */
public class RepositoryException extends SummarizedException {
    protected String repoHostName;
    protected String repoHostUrl;

    /**
     * @param message      - message of the exception
     * @param cause        - cause of the exception
     * @param repoHostName - the name of the repository host
     * @param repoHostUrl  - the URL of the repository
     */
    public RepositoryException(String message, Throwable cause, String repoHostName, String repoHostUrl) {
        super(message, cause);
        this.repoHostName = repoHostName;
        this.repoHostUrl = repoHostUrl;
        lineList.add(0, "The problem occurred while working with the " + repoHostName + " url: " + repoHostUrl);
    }

}

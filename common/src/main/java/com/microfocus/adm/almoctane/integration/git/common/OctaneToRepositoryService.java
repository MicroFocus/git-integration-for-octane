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

package com.microfocus.adm.almoctane.integration.git.common;

/**
 * Base class used for executing actions on a repository and Octane
 */
public abstract class OctaneToRepositoryService {
    protected OctaneService octaneService;
    protected RepositoryConnectionAdapter repositoryConnectionAdapter;

    /**
     * @param octaneService               - instance of an object which can perform Octane operations
     * @param repositoryConnectionAdapter - instance of an object which can perform actions on a repository
     */
    public OctaneToRepositoryService(OctaneService octaneService, RepositoryConnectionAdapter repositoryConnectionAdapter) {
        this.octaneService = octaneService;
        this.repositoryConnectionAdapter = repositoryConnectionAdapter;
    }

    /**
     * Performs the needed actions in both the repository and Octane
     */
    public abstract void execute();

}

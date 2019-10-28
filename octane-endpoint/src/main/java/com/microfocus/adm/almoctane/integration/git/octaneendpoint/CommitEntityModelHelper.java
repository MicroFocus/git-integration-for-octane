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

package com.microfocus.adm.almoctane.integration.git.octaneendpoint;

import com.hpe.adm.nga.sdk.entities.OctaneCollection;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;

import java.util.function.Function;

import static com.microfocus.adm.almoctane.integration.git.octaneendpoint.OctaneField.*;

/**
 * Facilitates the work with EntityModels that are commits.
 */
public class CommitEntityModelHelper {
    private EntityModel entityModel;

    public CommitEntityModelHelper(EntityModel entityModel) {
        this.entityModel = entityModel;
    }

    /**
     * Returns a Commit object containing the commit hash and the repository URL.
     *
     * @param getRepository - A function which returns the repository entity model.
     * @return - The Commit object containing the commit hash and the repository URL.
     */
    public Commit getCommitObjectFromEntityModel(Function<String, EntityModel> getRepository) {
        return new Commit(getCommitHash(), getRepositoryUrl(getRepository.apply(getCommitRepositoryId())));
    }

    /**
     * @return - The hash of the commit.
     */
    private String getCommitHash() {
        return entityModel.getValue(REVISION).getValue().toString();
    }

    /**
     * @return - The repository id (the repository entity from Octane) where the commit was made.
     */
    @SuppressWarnings("unchecked")
    private String getCommitRepositoryId() {
        return ((OctaneCollection<EntityModel>) entityModel.getValue(REPOSITORIES).getValue()).iterator().next().getId();
    }

    /**
     * @param repositoryEntityModel - A repository entity model.
     * @return - The repository URL.
     */
    private String getRepositoryUrl(EntityModel repositoryEntityModel) {
        return repositoryEntityModel.getValue(URL).getValue().toString();
    }
}

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

import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity;
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;

import java.util.List;

/**
 * All the actions that we need to perform on a given repository
 */
public interface RepositoryConnectionAdapter {

    /**
     * @param commits - List of commits.
     * @return - List of all the pull requests related to the commits
     */
    List<PullRequest> getPullRequestsFromCommits(List<Commit> commits);

    /**
     * @param octaneEntity - the Octane entity for which the branch will be created
     * @return - the url used for creating a new branch in the repository
     */
    String getCreateBranchUrl(OctaneEntity octaneEntity);

    /**
     * @param commits - List of commits.
     * @return - A list with Branches related to the given commits.
     */
    List<Branch> getBranchesFromCommits(List<Commit> commits);
}

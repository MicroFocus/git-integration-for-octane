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

import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;

import java.util.List;
import java.util.Set;

/**
 * All the actions that we need to perform on a given repository
 */
public interface RepositoryConnectionAdapter {

    /**
     * @param commits - list of commits for which the pull request
     * @return - list of all the pull requests related to the commits
     */
    List<PullRequest> getPullRequestsFromCommits(List<Commit> commits);

}

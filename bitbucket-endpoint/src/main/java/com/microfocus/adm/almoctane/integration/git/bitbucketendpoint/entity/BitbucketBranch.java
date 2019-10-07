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
package com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity;

import com.google.api.client.util.Key;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.urls.BitbucketBrowseCodeUrl;
import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;

/**
 * Class used to describe the branch information received from the Bitbucket response
 */
public class BitbucketBranch implements Branch {
    @Key("id")
    private String id;

    @Key("displayId")
    private String branchName;

    private String branchSourceCodeUrl;

    private String repositoryName;

    /**
     * @return - The name of the branch.
     */
    @Override
    public String getBranchId() {
        return this.id;
    }

    /**
     * @return - The full name of the Bitbucket branch.
     */
    @Override
    public String getBranchName() {
        return this.branchName;
    }

    /**
     * Sets the branch browse URL.
     *
     * @param server      - The Bitbucket server.
     * @param projectName - The Bitbucket projectName name.
     */
    public void setBrowseCodeOnBranchUrl(String server, String projectName) {
        BitbucketBrowseCodeUrl url = new BitbucketBrowseCodeUrl(server);
        url.setRawPath(String.format("/projects/%s/repos/%s/browse", projectName, repositoryName));

        this.branchSourceCodeUrl = url.setBranch(this).toString();
    }

    /**
     * @param repositoryName - The name of the Bitbucket repository.
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    /**
     * @return - The name of the Bitbucket repository.
     */
    @Override
    public String getRepositoryName() {
        return this.repositoryName;
    }

    /**
     * @return - The source code URL.
     */
    @Override
    public String geBranchSourceCodeUrl() {
        return this.branchSourceCodeUrl;
    }

    /**
     * @param obj - A branch object.
     * @return  - 0 if the branch ids are the same
     *          - 1 if the branch id is lexicographically greater than the argument's branch id
     *          - -1 if the branch id is lexicographically less than the argument's branch id
     */
    @Override
    public int compareTo(Branch obj) {
        return obj.getBranchId().compareTo(id);
    }

    /**
     * @return - The branch id's hash code.
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @param obj - A branch object
     * @return  - true if the branch ids are lexicographically equal
     *          - false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BitbucketBranch) {
            return ((BitbucketBranch) obj).getBranchId().equals(id);
        }
        return false;
    }
}
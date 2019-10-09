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

package com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.urls;

import com.microfocus.adm.almoctane.integration.git.common.exceptions.InvalidUrlRepositoryException;

/**
 * url for a specific repository
 */
public class BitbucketRepositoryUrl extends BitbucketUrl {

    private String projectSlug;
    private String repositorySlug;

    /**
     * @param serverUrl    - base url of the Bitbucket server
     * @param gitCloneLink - the git clone url to the repository
     */
    public BitbucketRepositoryUrl(String serverUrl, String gitCloneLink) {
        super(serverUrl);
        try {
            // at 0, http/https, 1 is empty, 2 is server url, 3 is scm, 4 is project, 5 is repo
            String[] link = gitCloneLink.replace(".git", "").split("/");
            projectSlug = link[4];
            repositorySlug = link[5];

        } catch (IndexOutOfBoundsException e) {
            throw new InvalidUrlRepositoryException("Git clone link has an unexpected stricture. Got " + gitCloneLink +
                    " and was expecting the following structure: http(s)://<serverUrl>/scm/<projectSlug>/<repoSlug>.git.\n\t\t" +
                    "Additional information: " + e.getMessage(),
                    e, SERVER_HOST_NAME, gitCloneLink, getServerBaseUrl());
        }
    }

    /**
     * @return - the Bitbucket project slug
     */
    public String getProjectSlug() {
        return projectSlug;
    }

    /**
     * @param projectSlug - the Bitbucket project slug
     */
    public void setProjectSlug(String projectSlug) {
        this.projectSlug = projectSlug;
    }

    /**
     * @return - the Bitbucket repository slug
     */
    public String getRepositorySlug() {
        return repositorySlug;
    }

    /**
     * @param repositorySlug - the Bitbucket repository slug
     */
    public void setRepositorySlug(String repositorySlug) {
        this.repositorySlug = repositorySlug;
    }

    /**
     * Sets the current url to the one needed for getting branch information related to a commit using the following
     * Bitbucket Server api:
     * /rest/branch-utils/1.0/projects/{projectKey}/repos/{repositorySlug}/branches/info/{commitId}
     *
     * @param commitHash - hash of the commit for which to bring the branch information
     */
    public void setBranchUtilsUrl(String commitHash) {
        this.setRawPath("/rest/branch-utils/1.0/projects/" + projectSlug + "/repos/" + repositorySlug +
                "/branches/info/" + commitHash);
    }

    /**
     * Return the string url to the source code browsing page in Bitbucket Server for a specific branch
     *
     * @param branchId - branch for which to get the
     * @return - url to branch source code
     */
    public String getBranchSourceCodeUrl(String branchId) {
        return String.format("%s?at=%s", getSourceCodeUrl(), branchId);
    }

    /**
     * @return - url to the browse page of the source code in this Bitbucket Server repository
     */
    public String getSourceCodeUrl() {
        return String.format("%s/projects/%s/repos/%s/browse", getServerBaseUrl(), projectSlug, repositorySlug);
    }

    /**
     * Sets the current url to the one needed for getting pull requests related to a commit using the following
     * Bitbucket Server api:
     * /rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits/{commitId}/pull-requests
     *
     * @param commitHash - hash of the commit for which to bring the pull requests
     */
    public void setPullRequestsForCommitUrl(String commitHash) {
        this.setRawPath("/rest/api/latest/projects/" + projectSlug + "/repos/" + repositorySlug + "/commits/" +
                commitHash + "/pull-requests");
    }

    /**
     * @return - clone of the current object
     */
    @Override
    public BitbucketRepositoryUrl clone() {
        BitbucketRepositoryUrl clone = (BitbucketRepositoryUrl) super.clone();
        clone.projectSlug = projectSlug;
        clone.repositorySlug = repositorySlug;
        return clone;
    }
}

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

import java.util.Set;

/**
 * Class used to describe the Bitbucket response received by calling the following Bitbucket api:
 * /rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits/{commitId}/pull-requests.
 */
public class BitbucketPullRequestResponse extends BitbucketResponse{


    @Key("values")//the value field in the response contains the pull requests
    private Set<BitbucketPullRequest> pullRequests;

    /**
     *
     * @return - true if the response was the last page
     *         - false otherwise
     */
    public boolean isLastPage() {
        return isLastPage;
    }


    /**
     *
     * @return - set of pull requests received from Bitbucket
     */
    public Set<BitbucketPullRequest> getPullRequests() {
        return pullRequests;

    }

    /**
     *
     * @return - the Bitbucket page from which to get the rest of the request response
     */
    public int getNextPageStart() {
        return nextPageStart;
    }
}

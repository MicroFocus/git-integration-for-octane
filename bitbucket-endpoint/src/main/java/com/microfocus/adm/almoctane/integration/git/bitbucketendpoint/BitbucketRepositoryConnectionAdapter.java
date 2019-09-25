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

package com.microfocus.adm.almoctane.integration.git.bitbucketendpoint;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketPullRequest;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketPullRequestResponse;
import com.microfocus.adm.almoctane.integration.git.common.RepositoryConnectionAdapter;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


/**
 * Class used to perform calls to a single Bitbucket server
 */
public class BitbucketRepositoryConnectionAdapter implements RepositoryConnectionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitbucketRepositoryConnectionAdapter.class);
    private HttpTransport httpTransport;
    private JsonFactory jsonFactory = new JacksonFactory();
    private String personalAuthenticationToken;
    private BitbucketUrl serverRestUrl;
    public final String SERVER_HOST_NAME = "Bitbucket Server";

    /**
     *
     * @param server - the link to the Bitbucket Server e.g. http://myBitBucketServer:Port
     * @param personalAuthenticationToken - the access token of the "global" user which has read (used for getting pull
     *                                    requests) and write (for creating branches) access on all the needed projects
     *                                    and repositories.
     * @param httpTransport - the http transport to be used
     *                      (ideally, only one http transport instance exists in the whole project)
     */
    public BitbucketRepositoryConnectionAdapter(String server, String personalAuthenticationToken, HttpTransport httpTransport) {
        serverRestUrl = new BitbucketUrl(server);
        this.httpTransport = httpTransport;
        this.personalAuthenticationToken = "Bearer " + personalAuthenticationToken;
    }

    /**
     * Used for getting all the unique pull requests that contain at least one of the commits in the list.
     *
     * @param commits - list of commits for which the pull request
     * @return - sorted list of all the pull requests related to the commits
     */
    @Override
    public List<PullRequest> getPullRequestsFromCommits(List<Commit> commits) {
        //get pull requests with no duplicates
        Set<PullRequest> pullRequests = new HashSet<>();
        commits.forEach((commit) ->
                {
                    Set<BitbucketPullRequest> bitbucketPullRequests;
                    try {
                        bitbucketPullRequests = getPullRequestsFromCommit(commit);
                        pullRequests.addAll(bitbucketPullRequests);
                    } catch (Exception e) {
                        LOGGER.warn("Pull requests related to the commit with the hash " + commit.getHash() +
                                " cloned from " + commit.getRepoLink() + " could not be retrieved!");

                        if (!(e instanceof InvalidUrlRepositoryException))
                            throw e;

                        LOGGER.warn("Continuing the retrieval of other commits, but an InvalidUrlRepositoryException " +
                                "occurred while using an url to retrieve the pull requests related to the commit " +
                                "with the hash " + commit.getHash() + " and cloned from the repository " +
                                commit.getRepoLink() + "\n\t\tException message: " + e.getMessage() +
                                "\n\t\tStacktrace: " + Arrays.toString(e.getStackTrace()));

                    }

                }
        );

        //sort pull requests
        List<PullRequest> sortedPullRequests = new LinkedList<>(pullRequests);
        sortedPullRequests.sort(Comparable::compareTo);

        return sortedPullRequests;
    }

    /**
     * Builds the url needed to use the Bitbucket api:
     * /rest/api/1.0/projects/{projectKey}/repos/{repositorySlug}/commits/{commitId}/pull-requests
     * @param commit - the commit for which to get the pull requests
     * @return the URL for making the request
     */
    public BitbucketUrl getPullRequestsUrl(Commit commit) {

        try {
            // at 0, http/https, 1 is empty, 2 is server url, 3 is scm, 4 is project, 5 is repo
            String[] link = commit.getRepoLink().replace(".git", "").split("/");
            BitbucketUrl pullRequestRequestUrl = serverRestUrl.clone();
            String path = "/projects/" +
                    link[4] +
                    "/repos/" +
                    link[5] +
                    "/commits/" +
                    commit.getHash() +
                    "/pull-requests";

            pullRequestRequestUrl.appendRawPath(path);

            return pullRequestRequestUrl;
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidUrlRepositoryException("Commit has unexpected url structure. Got " + commit.getRepoLink() +
                    " and was expecting the following structure: http(s)://<serverUrl>/scm/<projectSlug>/<repoSlug>.git.\n\t\t" +
                    "Additional information: " + e.getMessage(),
                    e, SERVER_HOST_NAME, commit.getRepoLink(), serverRestUrl.getHost());
        }
    }

    /**
     *
     * @param commit - the commit for which to get the pull requests
     * @return all the pull requests which contain the given commit
     */
    private Set<BitbucketPullRequest> getPullRequestsFromCommit(Commit commit) {

        BitbucketUrl pullRequestRequestUrl = getPullRequestsUrl(commit);

        Set<BitbucketPullRequest> pullRequests = new HashSet<>();

        HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory((HttpRequest request) ->
                request.setParser(new JsonObjectParser(jsonFactory)));

        BitbucketPullRequestResponse bitbucketPullRequestResponse;
        try {
            do {
                HttpRequest httpRequest = httpRequestFactory.buildGetRequest(pullRequestRequestUrl);
                httpRequest.getHeaders().setAuthorization(personalAuthenticationToken);
                bitbucketPullRequestResponse = httpRequest.execute().parseAs(BitbucketPullRequestResponse.class);
                pullRequests.addAll(bitbucketPullRequestResponse.getPullRequests());
                pullRequestRequestUrl.setStart(bitbucketPullRequestResponse.getNextPageStart());
            }
            while (!bitbucketPullRequestResponse.isLastPage());
        } catch (HttpResponseException e) {
            switch (e.getStatusCode()) {
                case 401:
                    //do not put actual key in the logs
                    throw new UnauthorizedUserRepositoryException("The Personal Access Token key used did not have " +
                            "enough permissions to get the pull requests.\n\t\tAdditional information: " + e.getMessage(),
                            e, SERVER_HOST_NAME, serverRestUrl.getHost());
                case 404:
                    String fullUrl = pullRequestRequestUrl.getHost() + pullRequestRequestUrl.getRawPath();
                    throw new InvalidUrlRepositoryException("The url " + fullUrl + " could not be found in the " +
                            serverRestUrl.getHost() + " Bitbucket server\n\t\tAdditional information: " + e.getMessage(),
                            e, SERVER_HOST_NAME, fullUrl, serverRestUrl.getHost());
                default:
                    throw new RepositoryException("The response from the Bitbucket server was an error message\n\t\t" +
                            "Additional information: " + e.getMessage(),
                            e, SERVER_HOST_NAME, serverRestUrl.getHost());
            }
        } catch (IOException e) {

            throw new RequestException("An exception occurred while connecting to the Bitbucket server\n\t\t" +
                    "Additional information: " + e.getMessage(),
                    e);
        }


        return pullRequests;
    }
}

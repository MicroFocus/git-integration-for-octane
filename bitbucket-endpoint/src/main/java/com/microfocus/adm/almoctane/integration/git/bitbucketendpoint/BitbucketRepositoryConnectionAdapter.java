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
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketBranch;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketBranchResponse;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketPullRequest;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity.BitbucketPullRequestResponse;
import com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.urls.BitbucketRepositoryUrl;
import com.microfocus.adm.almoctane.integration.git.common.RepositoryConnectionAdapter;
import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity;
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.InvalidUrlRepositoryException;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.RepositoryException;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.RequestToRepositoryException;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.UnauthorizedUserRepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.urls.BitbucketUrl.SERVER_HOST_NAME;

/**
 * Class used to perform calls to a single Bitbucket server
 */
public class BitbucketRepositoryConnectionAdapter implements RepositoryConnectionAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BitbucketRepositoryConnectionAdapter.class);
    private HttpTransport httpTransport;
    private JsonFactory jsonFactory = new JacksonFactory();
    private String personalAuthenticationToken;
    private String serverUrlString;

    /**
     * @param server                      - the link to the Bitbucket Server e.g. http://myBitBucketServer:Port
     * @param personalAuthenticationToken - the access token of the "global" user which has read access on all the
     *                                    needed projects and repositories.
     * @param httpTransport               - the http transport to be used
     *                                    (ideally, only one http transport instance exists in the whole project)
     */
    public BitbucketRepositoryConnectionAdapter(String server, String personalAuthenticationToken, HttpTransport httpTransport) {
        serverUrlString = server;
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

                        LOGGER.warn("Continuing the retrieval of pull requests from other commits, but an " +
                                "InvalidUrlRepositoryException occurred while using an url to retrieve the pull " +
                                "requests related to the commit with the hash " + commit.getHash() + " and cloned" +
                                " from the repository " + commit.getRepoLink() + "\n\t\tException message: " +
                                e.getMessage() + "\n\t\tStacktrace: " + Arrays.toString(e.getStackTrace()));
                    }
                }
        );

        //sort pull requests
        List<PullRequest> sortedPullRequests = new LinkedList<>(pullRequests);
        sortedPullRequests.sort(Comparable::compareTo);
        LOGGER.info(String.format("Got %s pull requests form Bitbucket", sortedPullRequests.size()));

        return sortedPullRequests;
    }

    /**
     * Returns an url to Bitbucket branch creation page with name and type values already filled in
     *
     * @param octaneEntity - Octane entity containing the name and type used to fill in values in the creation page
     * @return - url to Bitbucket creation page
     */
    @Override
    public String getCreateBranchUrl(OctaneEntity octaneEntity) {
        String bitbucketType;
        switch (octaneEntity.getType()) {
            case DEFECT:
                bitbucketType = "bug";
                break;
            case FEATURE:
                bitbucketType = "story";
                break;
            default:
                bitbucketType = "custom";

        }
        return serverUrlString +
                "/plugins/servlet/create-branch?issueType=" +
                bitbucketType +
                "&issueSummary=" +
                octaneEntity.getId() +
                "-" +
                octaneEntity.getName();
    }

    /**
     * @param commits - List of commits.
     * @return - A list with Branches related to the given commits.
     */
    @Override
    public List<Branch> getBranchesFromCommits(List<Commit> commits) {
        Set<Branch> branches = new HashSet<>();
        commits.forEach((commit) ->
                {
                    Set<BitbucketBranch> bitbucketBranches;
                    try {
                        bitbucketBranches = getBranchesFromCommit(commit);
                        branches.addAll(bitbucketBranches);
                    } catch (Exception e) {
                        LOGGER.warn("Branches related to the commit with the hash " + commit.getHash() +
                                " cloned from " + commit.getRepoLink() + " could not be retrieved!");

                        if (!(e instanceof InvalidUrlRepositoryException))
                            throw e;

                        LOGGER.warn("Continuing the retrieval of branch information from other commits, but an " +
                                "InvalidUrlRepositoryException occurred while using an url to retrieve the branches " +
                                "related to the commit with the hash " + commit.getHash() + " and cloned from the " +
                                "repository " + commit.getRepoLink() + "\n\t\tException message: " + e.getMessage() +
                                "\n\t\tStacktrace: " + Arrays.toString(e.getStackTrace()));
                    }
                }
        );

        List<Branch> sortedBranches = new LinkedList<>(branches);
        sortedBranches.sort(Comparable::compareTo);
        LOGGER.info(String.format("Got %s branches from Bitbucket", sortedBranches.size()));

        return sortedBranches;
    }

    /**
     * @param commit - the commit for which to get the pull requests
     * @return all the pull requests which contain the given commit
     */
    private Set<BitbucketPullRequest> getPullRequestsFromCommit(Commit commit) {
        BitbucketRepositoryUrl pullRequestRequestUrl = new BitbucketRepositoryUrl(serverUrlString, commit.getRepoLink());
        pullRequestRequestUrl.setPullRequestsForCommitUrl(commit.getHash());

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
            handleHttpResponseException(pullRequestRequestUrl, e);
        } catch (IOException e) {
            throw new RequestToRepositoryException("An exception occurred while connecting to the Bitbucket server\n\t\t" +
                    "Additional information: " + e.getMessage(),
                    e,
                    SERVER_HOST_NAME,
                    serverUrlString);
        }

        return pullRequests;
    }

    /**
     * @param commit - The commit for which to get the branches
     * @return - All the branches which contain the given commit
     */
    private Set<BitbucketBranch> getBranchesFromCommit(Commit commit) {
        BitbucketRepositoryUrl repoUrl = new BitbucketRepositoryUrl(serverUrlString, commit.getRepoLink());
        repoUrl.setBranchUtilsUrl(commit.getHash());

        Set<BitbucketBranch> branches = new HashSet<>();

        HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory((HttpRequest request) ->
                request.setParser(new JsonObjectParser(jsonFactory)));

        BitbucketBranchResponse bitbucketBranchResponse;
        try {
            do {
                HttpRequest httpRequest = httpRequestFactory.buildGetRequest(repoUrl);
                httpRequest.getHeaders().setAuthorization(personalAuthenticationToken);
                bitbucketBranchResponse = httpRequest.execute().parseAs(BitbucketBranchResponse.class);
                branches.addAll(bitbucketBranchResponse.getBranches());
                branches.forEach(branch -> {
                    branch.setRepositoryName(repoUrl.getRepositorySlug());
                    String branchSourceCodeUrl = repoUrl.getBranchSourceCodeUrl(branch.getBranchId());
                    branch.setBranchSourceCodeUrl(branchSourceCodeUrl);
                });
                repoUrl.setStart(bitbucketBranchResponse.getNextPageStart());
            }
            while (!bitbucketBranchResponse.isLastPage());
        } catch (HttpResponseException e) {
            handleHttpResponseException(repoUrl, e);
        } catch (IOException e) {
            throw new RequestToRepositoryException("An exception occurred while connecting to the Bitbucket server\n\t\t" +
                    "Additional information: " + e.getMessage(),
                    e, SERVER_HOST_NAME, serverUrlString);
        }

        return branches;
    }

    /**
     * @param url - The Bitbucket URL.
     * @param e   - The HttpResponse exception.
     */
    private void handleHttpResponseException(BitbucketRepositoryUrl url, HttpResponseException e) {
        switch (e.getStatusCode()) {
            case 401:
            case 403:
                //do not put actual key in the logs
                throw new UnauthorizedUserRepositoryException("The Personal Access Token key used did not have " +
                        "enough permissions to get the pull requests.\n\t\tAdditional information: " + e.getMessage(),
                        e, SERVER_HOST_NAME, serverUrlString);
            case 404:
                String fullUrl = url.toString();
                throw new InvalidUrlRepositoryException("The url " + fullUrl + " could not be found in the " +
                        serverUrlString + " Bitbucket server\n\t\tAdditional information: " + e.getMessage(),
                        e, SERVER_HOST_NAME, fullUrl, serverUrlString);
            default:
                throw new RepositoryException("The response from the Bitbucket server was an error message\n\t\t" +
                        "Additional information: " + e.getMessage(),
                        e, SERVER_HOST_NAME, serverUrlString);
        }
    }
}

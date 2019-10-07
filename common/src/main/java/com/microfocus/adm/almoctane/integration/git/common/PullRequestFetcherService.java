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

import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneUDF;
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.SummarizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Class used to fetch the pull requests from the repository into Octane
 */
public class PullRequestFetcherService extends OctaneToRepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestFetcherService.class);

    /**
     * @see OctaneToRepositoryService
     */
    public PullRequestFetcherService(OctaneService octaneService, RepositoryConnectionAdapter repositoryConnectionAdapter) {
        super(octaneService, repositoryConnectionAdapter);
    }

    /**
     * Performs a call to Octane to get the commits. Using the gathered commits, a request is made to the repository to
     * get all the pull request related to those commits. A string containing an html page with the collected
     * pull requests is built and posted back to the UDF created in Octane.
     */
    @Override
    public void execute() {
        StringBuilder responseHtml = CommonUtils.getLastModifiedHtmlString();

        try {
            List<PullRequest> pullRequests = repositoryConnectionAdapter.getPullRequestsFromCommits(octaneService.getCommits());

            //put on separate lines a link to each pull request
            pullRequests.forEach(pr -> responseHtml.append(
                    String.format("<p><a href=\"%s\"> %s</a> - %s</p>", pr.getPullRequestLink(), pr.getPullRequestName(), pr.getPullRequestState())));

        } catch (SummarizedException e) {
            LOGGER.error("Exception while getting the pull requests from the repository\n\t\t Additional information: " +
                    e.getMessage() + "Stacktrace: " + Arrays.toString(e.getStackTrace()));

            //add a summarized error message to the response
            List<String> lineList = e.getSummary();
            responseHtml.append("<p>Something went wrong:");
            lineList.forEach(line -> responseHtml.append("<p>").append(line).append("</p>"));
            responseHtml.append("</p>");

        } finally {
            responseHtml.append("</body></html>");
            octaneService.postToUdf(responseHtml.toString(), OctaneUDF.Type.PULL_REQUEST);
        }
    }
}

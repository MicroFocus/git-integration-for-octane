package com.microfocus.adm.almoctane.integration.git.common;

import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneUDF;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.SummarizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Class used to fetch the branch information from a repository into Octane
 */
public class BranchInformationFetcherService extends OctaneToRepositoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PullRequestFetcherService.class);

    /**
     * @see OctaneToRepositoryService
     */
    public BranchInformationFetcherService(OctaneService octaneService, RepositoryConnectionAdapter repositoryConnectionAdapter) {
        super(octaneService, repositoryConnectionAdapter);
    }

    /**
     * Performs a call to Octane to get the commits. Using the gathered commits, a request is made to the repository to
     * get all the branches related to those commits. A string containing an html page with the collected
     * branches is built and posted back to the UDF created in Octane.
     */
    @Override
    public void execute() {
        StringBuilder responseHtml = CommonUtils.getLastModifiedHtmlString();

        try {
            List<Branch> branches = repositoryConnectionAdapter.getBranchesFromCommits(octaneService.getCommits());

            //put on separate lines a link to each branch
            branches.forEach(br -> responseHtml.append(
                    String.format("<p><a href=\"%s\"> %s</a> - %s</p>", br.getBrowseCodeOnBranchUrl(), br.getBranchName(), br.getRepositoryName())));

        } catch (SummarizedException e) {
            LOGGER.error(String.format("Exception while getting the branches from the repository\n\t\t Additional information: %s Stacktrace:\n %s",
                    e.getMessage(), Arrays.toString(e.getStackTrace())));

            //add a summarized error message to the response
            List<String> lineList = e.getSummary();
            responseHtml.append("<p>Something went wrong:");
            lineList.forEach(line -> responseHtml.append("<p>").append(line).append("</p>"));
            responseHtml.append("</p>");

        } finally {
            responseHtml.append("</body></html>");
            octaneService.postToUdf(responseHtml.toString(), OctaneUDF.Type.BRANCH);
        }
    }
}

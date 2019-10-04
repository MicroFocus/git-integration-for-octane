package com.microfocus.adm.almoctane.integration.git.common;

import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneUDF;
import com.microfocus.adm.almoctane.integration.git.common.exceptions.SummarizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
        List<Commit> commits = octaneService.getCommits();

        LOGGER.info("Got " + commits.size() + " commits from Octane.");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());


        StringBuilder responseHtml = new StringBuilder("<html><body><p><b>Last updated on: ");
        //add the current time as the time of the last update
        responseHtml.append(formatter.format(date));
        responseHtml.append("</b></p>");

        try {
            List<Branch> pullRequests = repositoryConnectionAdapter.getBranchesFromCommits(commits);
            LOGGER.info("Got " + pullRequests.size() + " pull requests from the Repository.");

            //put on separate lines a link to each pull request
            pullRequests.forEach(pr -> responseHtml.append("<p><a href=\"")
                    .append(pr.getBrowseCodeOnBranchUrl())
                    .append("\">")
                    .append(pr.getBranchName())
                    .append("</a> - ").append(pr.getRepositoryName())
                    .append("</p>"));

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
            octaneService.postToUdf(responseHtml.toString(), OctaneUDF.Type.BRANCH);
        }
    }
}

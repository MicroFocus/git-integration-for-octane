package com.microfocus.adm.almoctane.integration.git.common;

import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity;

public class BranchCreationUrlFetcherService extends OctaneToRepositoryService {

    private String branchCreationUrl;

    /**
     * @param octaneService               - instance of an object which can perform Octane operations
     * @param repositoryConnectionAdapter - instance of an object which can perform actions on a repository
     */
    public BranchCreationUrlFetcherService(OctaneService octaneService, RepositoryConnectionAdapter repositoryConnectionAdapter) {
        super(octaneService, repositoryConnectionAdapter);
    }

    /**
     * Gets the converted OctaneEntity from Octane and uses it to get the branch creation url to the repository
     */
    @Override
    public void execute() {
        OctaneEntity octaneEntity = octaneService.getOctaneEntity();
        branchCreationUrl = repositoryConnectionAdapter.getCreateBranchUrl(octaneEntity);
    }

    /**
     * @return - the url used for creating a new branch in the repository
     */
    public String getBranchCreationUrl() {
        return branchCreationUrl;
    }
}

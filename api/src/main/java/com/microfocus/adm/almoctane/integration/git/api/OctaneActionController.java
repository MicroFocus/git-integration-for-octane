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

package com.microfocus.adm.almoctane.integration.git.api;

import com.microfocus.adm.almoctane.integration.git.common.*;
import com.microfocus.adm.almoctane.integration.git.config.Factory;
import com.microfocus.adm.almoctane.integration.git.config.FactoryException;
import com.microfocus.adm.almoctane.integration.git.config.OctanePoolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

@RestController
public class OctaneActionController {
    private static Logger LOGGER = LoggerFactory.getLogger(OctaneActionController.class);
    private final ExecutorService executorService;

    /**
     * @param executorService - executor service to which the requests will be submitted.
     */
    public OctaneActionController(@Qualifier("fixedThreadPool") ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * @param model       - the model of the request
     * @param ids         - the ids of the work items in Octane for which the request is made
     * @param dialogId    - id of the dialog which opens in Octane after a button request
     * @param server      - octane server form which the request is made
     * @param sharedSpace - octane shared space form which the request is made
     * @param workSpace   - octane workspace form which the request is made
     * @return -  the response page
     */
    @GetMapping("/pull-requests")
    public ModelAndView fetchPullRequestsIntoOctane(ModelMap model,
                                                    @RequestParam List<String> ids,
                                                    @RequestParam String dialogId,
                                                    @RequestParam String server,
                                                    @RequestParam Long sharedSpace,
                                                    @RequestParam Long workSpace) {
        //set the attributes which will modify the response page
        model.addAttribute("ids", ids);
        model.addAttribute("dialogId", dialogId);
        model.addAttribute("sharedSpace", sharedSpace);
        model.addAttribute("workSpace", workSpace);
        model.addAttribute("requestType", "pull-requests");

        executorService.submit(() -> {
            try {
                Factory factory = Factory.getInstance();
                ids.parallelStream().forEach((id) -> {

                    OctaneService octaneService = factory.getOctaneService(id, sharedSpace, workSpace, server);
                    RepositoryConnectionAdapter repositoryConnectionAdapter = factory.getImplementation();
                    OctaneToRepositoryService prfs = new PullRequestFetcherService(octaneService, repositoryConnectionAdapter);
                    prfs.execute();
                });

            } catch (OctanePoolException | IOException | FactoryException e) {
                LOGGER.error("Could not execute the request. \n\tMessage: " + e.getMessage() + "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
            } catch (Exception e) {
                LOGGER.error("An unknown exception occurred. \n\tMessage: " + e.getMessage() + "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
            }

        });

        return new ModelAndView("OctaneResponse", model);
    }


    /**
     * @param model       - the model of the request
     * @param ids         - the ids of the work items in Octane for which the request is made
     * @param dialogId    - id of the dialog which opens in Octane after a button request
     * @param server      - octane server form which the request is made
     * @param sharedSpace - octane shared space form which the request is made
     * @param workSpace   - octane workspace form which the request is made
     * @return -  the response page
     */
    @GetMapping("/branch-information")
    public ModelAndView fetchBranchInformationIntoOctane(ModelMap model,
                                                         @RequestParam List<String> ids,
                                                         @RequestParam String dialogId,
                                                         @RequestParam String server,
                                                         @RequestParam Long sharedSpace,
                                                         @RequestParam Long workSpace) {
        //set the attributes which will modify the response page
        model.addAttribute("ids", ids);
        model.addAttribute("dialogId", dialogId);
        model.addAttribute("sharedSpace", sharedSpace);
        model.addAttribute("workSpace", workSpace);
        model.addAttribute("requestType", "branch-information");

        executorService.submit(() -> {
            try {
                Factory factory = Factory.getInstance();

                ids.parallelStream().forEach((id) -> {
                    OctaneService octaneService = factory.getOctaneService(id, sharedSpace, workSpace, server);
                    RepositoryConnectionAdapter repositoryConnectionAdapter = factory.getImplementation();
                    OctaneToRepositoryService service = new BranchInformationFetcherService(octaneService, repositoryConnectionAdapter);
                    service.execute();
                });
            } catch (OctanePoolException | IOException | FactoryException e) {
                LOGGER.error("Could not execute the request. \n\tMessage: " + e.getMessage() + "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
            } catch (Exception e) {
                LOGGER.error("An unknown exception occurred. \n\tMessage: " + e.getMessage() + "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
            }

        });

        return new ModelAndView("OctaneResponse", model);
    }
}
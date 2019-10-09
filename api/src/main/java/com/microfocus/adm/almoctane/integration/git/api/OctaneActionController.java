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
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
     * @param server      - Octane server form which the request is made
     * @param sharedSpace - Octane shared space form which the request is made
     * @param workSpace   - Octane workspace form which the request is made
     * @return -  the response page
     */
    @GetMapping("/pull-requests")
    public ModelAndView fetchPullRequestsIntoOctane(ModelMap model,
                                                    @RequestParam List<String> ids,
                                                    @RequestParam String dialogId,
                                                    @RequestParam String server,
                                                    @RequestParam Long sharedSpace,
                                                    @RequestParam Long workSpace) {
        setModelAttributes(model,ids,dialogId,sharedSpace,workSpace,"pull-requests");

        executorService.submit(() -> ids.parallelStream().forEach((id) ->
                executeService(id, server, sharedSpace, workSpace, PullRequestFetcherService.class)));

        return new ModelAndView("OctaneResponse", model);
    }

    /**
     * @param id          - the id of the work item in Octane for which the request is made
     * @param server      - Octane server form which the request is made
     * @param sharedSpace - Octane shared space form which the request is made
     * @param workSpace   - Octane work space form which the request is made
     * @return - a redirect view to the branch creation page of the repository
     */
    @GetMapping("/create-branch-page")
    public RedirectView getCreateBranchPage(@RequestParam String id,
                                            @RequestParam String server,
                                            @RequestParam Long sharedSpace,
                                            @RequestParam Long workSpace) {
        BranchCreationUrlFetcherService service =
                executeService(id, server, sharedSpace, workSpace, BranchCreationUrlFetcherService.class);
        return service == null ? null : new RedirectView(service.getBranchCreationUrl());
    }


    /**
     * @param model       - the model of the request
     * @param ids         - the ids of the work items in Octane for which the request is made
     * @param dialogId    - id of the dialog which opens in Octane after a button request
     * @param server      - Octane server form which the request is made
     * @param sharedSpace - Octane shared space form which the request is made
     * @param workSpace   - Octane workspace form which the request is made
     * @return -  the response page
     */
    @GetMapping("/branch-information")
    public ModelAndView fetchBranchInformationIntoOctane(ModelMap model,
                                                         @RequestParam List<String> ids,
                                                         @RequestParam String dialogId,
                                                         @RequestParam String server,
                                                         @RequestParam Long sharedSpace,
                                                         @RequestParam Long workSpace) {
        setModelAttributes(model,ids,dialogId,sharedSpace,workSpace,"branch-information");

        executorService.submit(() -> ids.parallelStream().forEach((id) ->
                executeService(id, server, sharedSpace, workSpace, BranchInformationFetcherService.class)));
        return new ModelAndView("OctaneResponse", model);
    }


    private static void setModelAttributes(ModelMap model,
                                           List<String> ids,
                                           String dialogId,
                                           Long sharedSpace,
                                           Long workSpace,
                                           String type) {
        //set the attributes which will modify the response page
        model.addAttribute("ids", ids);
        model.addAttribute("dialogId", dialogId);
        model.addAttribute("sharedSpace", sharedSpace);
        model.addAttribute("workSpace", workSpace);
        model.addAttribute("requestType", type);

    }


    /**
     * Executes the requests received
     *
     * @param id          - the id of the work item in Octane for which the request is made
     * @param server      - Octane server form which the request is made
     * @param sharedSpace - Octane shared space form which the request is made
     * @param workSpace   - Octane workspace form which the request is made
     * @param serviceName - the class of the service to be executed
     * @param <T>         - extends OctaneToRepositoryService
     * @return - The instance of T which was used in the execution
     */
    private static <T extends OctaneToRepositoryService> T executeService(String id,
                                                                   String server,
                                                                   Long sharedSpace,
                                                                   Long workSpace,
                                                                   Class<T> serviceName) {
        try {
            Factory factory = Factory.getInstance();
            OctaneService octaneService = factory.getOctaneService(id, sharedSpace, workSpace, server);
            RepositoryConnectionAdapter repositoryConnectionAdapter = factory.getImplementation();
            Constructor<T> constructor = serviceName.getConstructor(OctaneService.class,
                    RepositoryConnectionAdapter.class);
            T service = constructor.newInstance(octaneService, repositoryConnectionAdapter);
            service.execute();
            return service;
        } catch (OctanePoolException | IOException | FactoryException | NoSuchMethodException |
                IllegalAccessException | InstantiationException | InvocationTargetException e) {
            LOGGER.error("Could not execute the request. \n\tMessage: " + e.getMessage() +
                    "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            LOGGER.error("An unknown exception occurred. \n\tMessage: " + e.getMessage() +
                    "\n\tStacktrace: " + Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}
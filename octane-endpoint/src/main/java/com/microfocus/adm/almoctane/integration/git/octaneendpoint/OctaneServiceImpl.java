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

package com.microfocus.adm.almoctane.integration.git.octaneendpoint;

import com.hpe.adm.nga.sdk.entities.OctaneCollection;
import com.hpe.adm.nga.sdk.exception.OctanePartialException;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.microfocus.adm.almoctane.integration.git.common.OctaneService;
import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity.EntityType;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneUDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.microfocus.adm.almoctane.integration.git.octaneendpoint.OctaneFields.*;

/**
 * This class is using the OctaneRequestService to make requests to Octane
 * and implement the actions described in OctaneService.
 */
public class OctaneServiceImpl extends OctaneService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctaneServiceImpl.class);
    private OctaneRequestService octaneRequestService;

    private Map<OctaneUDF.Type, OctaneUDF> userDefinedFields;

    public OctaneServiceImpl(
            String id,
            OctaneRequestService octaneRequestService,
            Map<OctaneUDF.Type, OctaneUDF> userDefinedFields
    ) {
        super(id);
        this.octaneRequestService = octaneRequestService;
        this.userDefinedFields = userDefinedFields;
    }

    /**
     * Octane commits are processed and then turned into Commit objects.
     *
     * @return - A list with Commits.
     */
    @Override
    public List<Commit> getCommits() {
        List<Commit> commits = new ArrayList<>();

        try {
            OctaneCollection<EntityModel> commitsPrimitiveEntityModels = octaneRequestService.getCommits(id);

            commitsPrimitiveEntityModels.forEach(getCommitInfoAndAddToCommitsList(commits));
            LOGGER.info(String.format("Got %s commits from Octane", commits.size()));
        } catch (OctaneRequestException e) {
            LOGGER.error(String.format("Exception caught during request. Message: %s. Commits will not be retrieved", e.getMessage()));
        }

        return commits;
    }

    /**
     * Updates the created udf with the given string
     *
     * @param string  - The string which will be used to update the udf.
     * @param udfType - The type of the UDF (i.e. pull request, branch)
     */
    @Override
    public void postToUdf(String string, OctaneUDF.Type udfType) {
        EntityModel workItem;
        try {
            workItem = octaneRequestService.getWorkItemEntityWithCommits(id);
        } catch (OctaneRequestException e) {
            LOGGER.error(String.format("Cannot post to udf. %s", e.getMessage()));
            return;
        }

        workItem.setValue(new StringFieldModel(userDefinedFields.get(udfType).getName(), string));
        try {
            octaneRequestService.updateEntityModel(id, workItem, userDefinedFields.get(udfType).getName(), userDefinedFields.get(udfType).getLabel());
        } catch (OctaneRequestException e) {
            LOGGER.error(String.format("Exception caught while posting to udf. Message: %s. Udf will not be updated", e.getMessage()));
        }
    }

    /**
     * Converts the entity subtype received form Octane into EntityType
     *
     * @param octaneType - the subtype of an entity
     * @return - converted type
     */
    public static EntityType convertEntityType(String octaneType) {
        switch (octaneType) {
            case DEFECT:
                return EntityType.DEFECT;
            case FEATURE:
                return EntityType.FEATURE;
            default:
                return EntityType.NOT_DEFINED;
        }
    }

    /**
     * Converts the entity with the id this.id to an OctaneEntity
     *
     * @return - converted OctaneEntity
     */
    @Override
    public OctaneEntity getOctaneEntity() {
        try {
            EntityModel octaneEntity = octaneRequestService.getWorkItemEntityWithName(id);
            String name = octaneEntity.getValue(NAME).getValue().toString();
            String octaneType = octaneEntity.getValue(SUBTYPE).getValue().toString();
            EntityType type = convertEntityType(octaneType);
            LOGGER.info("Got the name \"" + name + "\" and type \"" + octaneType + "\" for the entity with id: " + id +
                    ". Type was converted to \"" + type + "\"");
            return new OctaneEntity(octaneEntity. getId(), name, type);
        } catch (OctaneRequestException | OctanePartialException e) {
            LOGGER.error("Could not get name and entity subtype from octane! Message: "+e.getMessage()+"\n" +
                    "Stacktrace: "+ Arrays.toString(e.getStackTrace()) +"\nUsing default values for OctaneEntity.");
            return new OctaneEntity();
        }
    }

    /**
     * Returns a consumer which can be used in oder to get all the commits for an entity model.
     *
     * @param commits - The list with commits for an entity. This is an output parameter.
     * @return - A consumer which can be used in oder to get all the commits for an entity model.
     */
    private Consumer<EntityModel> getCommitInfoAndAddToCommitsList(List<Commit> commits) {
        return c -> {
            try {
                Commit com = new CommitEntityModelHelper(
                        octaneRequestService.getCommit(c.getId()))
                        .getCommitObjectFromEntityModel(octaneRequestService::getRepository);

                if (!commits.contains(com)) {
                    commits.add(com);
                }
            } catch (OctaneRequestException e) {
                LOGGER.error(String.format("Exception caught while getting commits info. Message: %s. Udf will not be updated", e.getMessage()));
            }
        };
    }
}

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

import com.hpe.adm.nga.sdk.Octane;
import com.hpe.adm.nga.sdk.entities.OctaneCollection;
import com.hpe.adm.nga.sdk.exception.OctanePartialException;
import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.nga.sdk.model.FieldModel;
import com.hpe.adm.nga.sdk.model.StringFieldModel;
import com.hpe.adm.nga.sdk.network.OctaneHttpRequest;
import com.hpe.adm.nga.sdk.network.google.GoogleHttpClient;
import com.hpe.adm.nga.sdk.query.Query;
import com.hpe.adm.nga.sdk.query.QueryMethod;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class is used for making Octane requests.
 */
public class OctaneRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OctaneRequestService.class);

    private GoogleHttpClient googleHttpClient;
    private String server;
    private long workspace;
    private long sharedSpace;

    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String PATH = "path";
    private static final String BODY = "body";
    private static final String EPIC = "epic";
    private static final String STORY = "story";
    private static final String LABEL = "label";
    private static final String DEFECT = "defect";
    private static final String FEATURE = "feature";
    private static final String SUBTYPE = "subtype";
    private static final String COMMITS = "commits";
    private static final String REVISION = "revision";
    private static final String WORK_ITEMS = "work_items";
    private static final String ENTITY_TYPE = "entity_type";
    private static final String SCM_COMMITS = "scm_commits";
    private static final String REPOSITORIES = "repositories";
    private static final String QUALITY_STORY = "quality_story";
    private static final String ENTITY_SUBTYPE = "entity_subtype";
    private static final String METADATA_FIELDS = "metadata_fields";
    private static final String SCM_REPOSITORIES = "scm_repositories";
    private static final String FIELD_METADATA_MEMO = "field_metadata_memo";

    private static final List<String> entityTypes = new ArrayList<>(Arrays.asList(EPIC, FEATURE, STORY, DEFECT, QUALITY_STORY));

    private Octane octane;

    public OctaneRequestService(Octane octane, GoogleHttpClient googleHttpClient, String server, long sharedSpace, long workspace) {
        this.octane = octane;
        this.googleHttpClient = googleHttpClient;
        this.server = server;
        this.sharedSpace = sharedSpace;
        this.workspace = workspace;
    }

    /**
     * Returns the entity model for a work item with a specific id.
     *
     * @param id - The id of the work item.
     * @return - The entity model of the work item.
     */
    public EntityModel getWorkItemEntity(String id) {
        return getEntityModel(id, WORK_ITEMS, COMMITS);
    }

    /**
     * Returns the entity model for a commit with a specific id.
     *
     * @param id - The id of the commit.
     * @return - The entity model of the commit.
     */
    public EntityModel getCommit(String id) {
        return getEntityModel(id, SCM_COMMITS, REPOSITORIES, REVISION);
    }

    /**
     * Returns the entity model for a repository with a specific id.
     *
     * @param id - The id of the repository.
     * @return - The entity model of the repository.
     */
    public EntityModel getRepository(String id) {
        return getEntityModel(id, SCM_REPOSITORIES, URL);
    }

    /**
     * Returns the entity model with the given id, type and fields.
     *
     * @param id - The id of the entity.
     * @param entityName - The entity type name.
     * @param fields - The fields that will be fetched.
     * @return - The entity model with the given id, type and fields.
     */
    private EntityModel getEntityModel(String id, String entityName, String... fields) {
        LOGGER.info(String.format("Getting %s wih id %s.", entityName, id));

        try {
            if (fields.length > 0) {
                return octane.entityList(entityName).at(id).get().addFields(fields).execute();
            } else {
                return octane.entityList(entityName).at(id).get().execute();
            }
        } catch (OctanePartialException e) {
            LOGGER.error(String.format(
                    "Error getting entity with id %s. Description: %s \nStack trace: %s", id,
                    getOctanePartialExceptionDescription(e), Arrays.toString(e.getStackTrace())));

            throw new OctaneRequestException(String.format("Request for entity with name %s and id %s failed", entityName, id));
        }
    }

    /**
     * Updates the pull requests udf. If the UDF does not exist, it will be created and added to the forms.
     * Moreover, a rule for 'Make read-only' will be created so that only Space Admins can modify the memo field.
     *
     * @param id - Id of the entity.
     * @param entityModel - The entity model oof the work item.
     * @param udfName - The name of the memo UDF.
     * @param udfLabel - The label of the UDF.
     */
    public void updateEntityModel(String id, EntityModel entityModel, String udfName, String udfLabel) {
        createUdfIdNotPresent(udfName, udfLabel);

        try {
            octane.entityList(WORK_ITEMS).at(id).update().entity(entityModel).execute();
            LOGGER.info(String.format("The %s UDF was updated work item with id %s", udfName, id));

        } catch (OctanePartialException e) {
            LOGGER.error(String.format(
                    "Error updating entity with id %s. Description: %s \nStack trace: %s", id,
                    getOctanePartialExceptionDescription(e),
                    Arrays.toString(e.getStackTrace())));

            throw new OctaneRequestException(String.format("Failed to update entity of type %s and id %s", entityModel.getType(), id));
        }
    }

    /**
     * Gets the commits from a specific entity with the given id.
     *
     * @param id - The id of the entity from where the commits will be fetched.
     * @return - An Octane collection with all the commits' entity models.
     */
    @SuppressWarnings("unchecked")
    public OctaneCollection<EntityModel> getCommits(String id) {
        LOGGER.info(String.format("Getting commits for entity with id %s", id));

        EntityModel entityModel = getEntityModel(id, WORK_ITEMS, SUBTYPE, COMMITS, PATH);
        String entitySubtype = entityModel.getValue(SUBTYPE).getValue().toString();

        if (entitySubtype.equals(FEATURE) || entitySubtype.equals(EPIC)) {
            return octane.entityList(
                    String.format("scm_commits?query=\"(stories={((path='%s*'))})\"", entityModel.getValue(PATH).getValue().toString()))
                    .get().execute();
        } else {
            return (OctaneCollection<EntityModel>) entityModel.getValue(COMMITS).getValue();
        }
    }

    /**
     * Checks if the UDF was already created. If it was already created, nothing will happen.
     * Otherwise, a memo UDF will be created with udfName and udfLabel given.
     * The UDF will then be added to the form.
     * Moreover, a rule for 'Make read-only' will be created so that only Space Admins can modify the memo field.
     *
     * @param udfName - The name of the memo UDF.
     * @param udfLabel - The label of the memo UDF
     */
    private void createUdfIdNotPresent(String udfName, String udfLabel) {
        for (String entityType : entityTypes) {
            EntityModel udf =
                    getUDFByNameAndEntityType(udfName, entityType);
            if (udf == null) {
                LOGGER.info(String.format("Creating udf with name %s and label %s for work_items", udfName, udfLabel));

                createUdf(udfName, udfLabel, entityType);
            }
        }
        googleHttpClient.signOut();
    }

    /**
     * Returns a UDF entity model if it exists in Octane.
     *
     * @param udfName - The name of the UDF we search for.
     * @param entityType -  The entity type.
     * @return - An entity model of the memo UDF.
     */
    private EntityModel getUDFByNameAndEntityType(String udfName, String entityType) {
        try {
            return octane.entityList(METADATA_FIELDS).get()
                    .addFields(NAME, ENTITY_TYPE).query(
                            Query.statement(NAME, QueryMethod.EqualTo, udfName)
                                    .and(ENTITY_TYPE, QueryMethod.EqualTo, entityType).build())
                    .execute().stream().findFirst().orElse(null);
        } catch (OctanePartialException e) {
            LOGGER.error(String.format(
                    "Error getting octane metadata. Description: %s \nStack trace: %s", getOctanePartialExceptionDescription(e), Arrays.toString(e.getStackTrace())));

            throw new OctaneRequestException("Entity metadata could not be retrieved");
        }
    }

    /**
     * A memo UDF will be created with udfName and udfLabel given for a specific entityType.
     * The UDF will then be added to the form. (What is also done here is getting all the forms for an entity type)
     * Moreover, a rule for 'Make read-only' will be created so that only Space Admins can modify the memo field.
     *
     * @param udfName - The name of the memo UDF.
     * @param udfLabel - The label of the memo UDF.
     * @param entityType - The entity type for which the memo UDF will be created.
     */
    private void createUdf(String udfName, String udfLabel, String entityType) {

        EntityModel udfEntityModel = createUDFEntityModel(udfName, udfLabel, entityType);

        try {
            OctaneCollection<EntityModel> forms = getFormsForEntity(entityType);

            if(!areFormsFromMasterWorkspace(forms)) {
                octane.entityList(METADATA_FIELDS).create()
                        .entities(Collections.singletonList(udfEntityModel))
                        .execute();
            } else {
                final OctaneHttpRequest.PostOctaneHttpRequest postOctaneHttpRequest = new OctaneHttpRequest.PostOctaneHttpRequest(
                        String.format("%s/api/shared_spaces/%s/workspaces/%s/metadata_fields", server, sharedSpace, 500),
                        OctaneHttpRequest.JSON_CONTENT_TYPE,
                        getJsonObjectFromEntityModel(udfEntityModel).toString()
                );
                googleHttpClient.execute(postOctaneHttpRequest);
            }

            LOGGER.info(String.format("UDF %s was created for entity type %s", udfName, entityType));

            addUDFToEditForm(entityType, udfName, forms);
            LOGGER.info(String.format("UDF %s was added to the edit form of %s", udfName, entityType));

            if(!areFormsFromMasterWorkspace(forms)) {
                makeUdfReadOnly(entityType, udfName, workspace);
            } else {
                makeUdfReadOnly(entityType, udfName, 500);
            }

            LOGGER.info(String.format("UDF %s is now read only for %s", udfName, entityType));
        } catch (OctanePartialException e) {
            LOGGER.error(String.format(
                    "Error creating udf for entity %s. Description: %s \nStack trace: %s", entityType, getOctanePartialExceptionDescription(e), Arrays.toString(e.getStackTrace())));

            throw new OctaneRequestException("Entity metadata could not be updated");
        }
    }

    /**
     * Adds a rule to make am UDF read only for a specific entityType.
     *
     * @param entityType - The entity type.
     * @param udfName - The name of the memo UDF.
     * @param workspace - The workspace where the rule will be created.
     */
    private void makeUdfReadOnly(String entityType, String udfName, long workspace) {
        String json = getReadOnlyRuleJson(entityType, udfName);

        final OctaneHttpRequest.PostOctaneHttpRequest putOctaneHttpRequest = new OctaneHttpRequest.PostOctaneHttpRequest(
                String.format("%s/api/shared_spaces/%s/workspaces/%s/business_rules", server, sharedSpace, workspace),
                OctaneHttpRequest.JSON_CONTENT_TYPE,
                json
        );
        googleHttpClient.execute(putOctaneHttpRequest);
    }

    /**
     * This is a JSON for the 'Make read-only' rule creation.
     *
     * @param entityType - The type of the entity.
     * @param udfName - The name of the memo UDF.
     * @return - The JSON for rule creation.
     */
    private String getReadOnlyRuleJson(String entityType, String udfName) {
        return String.format("{\"data\":[{\"is_active\":true,\"category\":\"%s\",\"event_names\":\"[]\"," +
                        "\"condition\":{\"not\":{\"contains\":[{\"fact\":\"session(\\\"current-user\\\")(\\\"roles\\\")\"}," +
                        "{\"fact\":\"customization(\\\"roles\\\")(\\\"role.shared.space.admin\\\")\"}]}}," +
                        "\"actions\":[{\"set-metadata\":{\"fact\":" +
                        "\"entity(\\\"work_item\\\")(\\\"customization\\\")(\\\"fields\\\")(\\\"%s\\\")(\\\"editable\\\")\"," +
                        "\"value\":{\"literal\":{\"type\":\"bool\",\"value\":false}}}}], \"order_id\" : 1}]}",
                entityType, udfName);
    }

    /**
     * The UDF will be added to the Edit form of the entity.
     *
     * @param entityType - The entity type.
     * @param udfName - The name of the memo UDF.
     * @param forms - All the forms for the entity type given.
     */
    private void addUDFToEditForm(String entityType, String udfName, OctaneCollection<EntityModel> forms) {
        for (EntityModel form : forms) {
            if (form.getValue("logical_name").getValue().toString().contains("0")) {
                postNewFormJsonAtWorkspaceLevel(form, addUDFToFormJson(udfName, form), Long.valueOf(form.getValue("workspace_id").getValue().toString()));

                LOGGER.info(String.format("UDF %s was added to the %s edit form with id %s.", udfName, entityType, form.getId()));
            }
        }
    }

    /**
     * Checks if any of the forms of an entity are in a master workspace.
     * This means we are in a Shared Space and we have to add the memo UDF in the master workspace
     *
     * @param forms - The forms of an entity.
     * @return  - true if any of the forms belong to the master workspace.
     *          - false otherwise.
     */
    private boolean areFormsFromMasterWorkspace(OctaneCollection<EntityModel> forms) {
        for (EntityModel form : forms) {
            if(form.getValue("workspace_id").getValue().toString().equals("500")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all the forms for a specific entityType.
     *
     * @param entityType - The type of the entity.
     * @return - All the forms for the entityType given.
     */
    private OctaneCollection<EntityModel> getFormsForEntity(String entityType) {
        LOGGER.info(String.format("Getting forms for entity with type %s", entityType));

        return octane.entityList("form_layouts")
                .get()
                .addFields(BODY)
                .query(Query.statement(ENTITY_SUBTYPE, QueryMethod.EqualTo, entityType).build())
                .execute();
    }

    /**
     * Makes a PUT request to update the form with the newly created memo UDF.
     *
     * @param form - The form that will be updated.
     * @param bodyJson - The JSON which contains the new UDF.
     * @param workspace - The workspace where the update will happen
     */
    private void postNewFormJsonAtWorkspaceLevel(EntityModel form, JSONObject bodyJson, long workspace) {
        final OctaneHttpRequest.PutOctaneHttpRequest putOctaneHttpRequest = new OctaneHttpRequest.PutOctaneHttpRequest(
                String.format("%s/api/shared_spaces/%s/workspaces/%s/form_layouts/%s", server, sharedSpace, workspace, form.getId()),
                OctaneHttpRequest.JSON_CONTENT_TYPE,
                String.format("{\"%s\":%s}", BODY, bodyJson)
        );

        googleHttpClient.execute(putOctaneHttpRequest);
    }

    /**
     * Returns the JSON for updating the forms with the new UDF.
     *
     * @param udfName - The name of the memo UDF.
     * @param form - The form where the UDF will be added.
     * @return - The JSON containing the new UDF.
     */
    private JSONObject addUDFToFormJson(String udfName, EntityModel form) {
        JSONPointer sectionFirstItemPointer = new JSONPointer("/layout/sections/0");
        JSONPointer fieldsPointer = new JSONPointer("/fields");

        JSONObject bodyJson = new JSONObject(form.getValue(BODY).getValue().toString());
        JSONObject udfFieldJson = new JSONObject(String.format("{\"size\":\"large\", \"name\":\"%s\"}", udfName));

        ((JSONArray) fieldsPointer.queryFrom(sectionFirstItemPointer.queryFrom(bodyJson))).put(udfFieldJson);
        return bodyJson;
    }

    /**
     * Returns an entity model for the memo UDF.
     *
     * @param udfName - The name of the memo UDF.
     * @param udfLabel - The label of the memo UDF.
     * @param entityType - The type of the entity.
     * @return - the entity model for the memo UDF.
     */
    private EntityModel createUDFEntityModel(String udfName, String udfLabel, String entityType) {
        EntityModel udfEntityModel = new EntityModel();

        udfEntityModel.setValue(new StringFieldModel(ENTITY_TYPE, entityType));
        udfEntityModel.setValue(new StringFieldModel(NAME, udfName));
        udfEntityModel.setValue(new StringFieldModel(LABEL, udfLabel));
        udfEntityModel.setValue(new StringFieldModel(SUBTYPE, FIELD_METADATA_MEMO));

        return udfEntityModel;
    }

    /**
     * Returns a JSON created from an entity model containing only StringFieldModels.
     *
     * @param entityModel - The entity model.
     * @return - The JSON created from the entity model.
     */
    private JSONObject getJsonObjectFromEntityModel(EntityModel entityModel) {
        JSONObject json = new JSONObject();

        for(FieldModel value : entityModel.getValues()) {
            json.put(value.getName(), value.getValue());
        }

        return new JSONObject().append("data", json);
    }

    /**
     * Returns the description extracted from the OctanePartialException.
     *
     * @param e - The OctanePartialException.
     * @return - The description of the exception.
     */
    private String getOctanePartialExceptionDescription(OctanePartialException e) {
        return e.getErrorModels().iterator().next().getValue("description").getValue().toString();
    }
}

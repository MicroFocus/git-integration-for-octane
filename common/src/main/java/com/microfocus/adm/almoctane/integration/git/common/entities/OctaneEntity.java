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

package com.microfocus.adm.almoctane.integration.git.common.entities;

/**
 * Used to represent an Octane entity
 */
public class OctaneEntity {
    private EntityType type;
    private String name;
    private String id;

    /**
     * @param name - name of the entity (equivalent of the name field in Octane)
     * @param type - type of the entity (equivalent of the subtype field in Octane)
     */
    public OctaneEntity(String id, String name, EntityType type) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    /**
     * Default Octane entity
     */
    public OctaneEntity() {
        this.name = "";
        this.id = "";
        type = EntityType.NOT_DEFINED;
    }

    /**
     * @return - type of the entity (equivalent of the subtype field in Octane)
     */
    public EntityType getType() {
        return type;
    }

    /**
     * @return - name of the entity (equivalent of the name field in Octane)
     */
    public String getName() {
        return name;
    }

    /**
     *  @return - id of the entity (equivalent of the id field in Octane)
     */
    public String getId() {
        return this.id;
    }

    /**
     * The entity types an OctaneEntity can have.
     */
    public enum EntityType {

        FEATURE("feature"),
        DEFECT("defect"),
        NOT_DEFINED("not_defined");

        private String value;

        EntityType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }

}

package com.microfocus.adm.almoctane.integration.git.common.entities;

/**
 * Used to represent an Octane entity
 */
public class OctaneEntity {
    private EntityType type;
    private String name;

    /**
     * @param name - name of the entity (equivalent of the name field in Octane)
     * @param type - type of the entity (equivalent of the subtype field in Octane)
     */
    public OctaneEntity(String name, EntityType type) {
        this.type = type;
        this.name = name;
    }

    /**
     * Default Octane entity
     */
    public OctaneEntity() {
        this.name = "";
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

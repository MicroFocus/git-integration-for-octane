package com.microfocus.adm.almoctane.integration.git.octaneendpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/*
This class is used to keep an evidence of the created UDFs for specific entities.
 */
public class UdfInformation {
    // the map is used to keep track which of the entities have the udf created
    private Map<String, AtomicBoolean> entitiesWithCreationStatus = new HashMap<>();

    public UdfInformation() {
        entitiesWithCreationStatus.put(OctaneField.EPIC, new AtomicBoolean(false));
        entitiesWithCreationStatus.put(OctaneField.FEATURE, new AtomicBoolean(false));
        entitiesWithCreationStatus.put(OctaneField.STORY, new AtomicBoolean(false));
        entitiesWithCreationStatus.put(OctaneField.DEFECT, new AtomicBoolean(false));
        entitiesWithCreationStatus.put(OctaneField.QUALITY_STORY, new AtomicBoolean(false));
    }

    /**
     * Returns the creation status of a UDF for a specific entityType.
     * @param entityType - The type of an Octane entity.
     * @return - the creation status of a UDF.
     */
    public boolean isCreated(String entityType) {
        return entitiesWithCreationStatus.get(entityType).get();
    }

    /**
     * Updates the creation status of the entity
     * @param entityType - The type of an Octane entity
     * @param value - The creation status which will be used for update.
     */
    public void updateCreationStatus(String entityType, boolean value) {
        entitiesWithCreationStatus.get(entityType).getAndSet(value);
    }
}

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

package com.microfocus.adm.almoctane.integration.git.common;

import com.microfocus.adm.almoctane.integration.git.common.entities.Commit;
import com.microfocus.adm.almoctane.integration.git.common.entities.OctaneEntity;

import java.util.List;

/**
 * Actions which can be performed in Octane on a specific work_item
 */
public abstract class OctaneService {
    protected String id;

    /**
     * @param id - the id of the entity in Octane
     */
    public OctaneService(String id) {
        this.id = id;
    }

    /**
     * Used to get all the commits that appear in Octane for the work_item with the current id
     *
     * @return - list of Commit objects
     */
    public abstract List<Commit> getCommits();

    /**
     * Post a string to the created UDF
     *
     * @param string - the string with which the UDF will be updated
     */
    public abstract void postToUdf(String string);

    /**
     * @return - OctaneEntity created from the Octane entity with the id this.id
     */
    public abstract OctaneEntity getOctaneEntity();

}

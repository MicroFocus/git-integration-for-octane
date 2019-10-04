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
 * Class modeling an Octane UDF.
 */
public class OctaneUDF {
    public Type getType() {
        return type;
    }

    public enum Type {
        PULL_REQUEST,
        BRANCH
    }

    private Type type;
    private String name;
    private String label;

    public OctaneUDF(String name, String label, Type type) {
        this.name = name;
        this.label = label;
        this.type = type;
    }

    /**
     * @return - The name of the UDF.
     */
    public String getName() {
        return name;
    }

    /**
     * @return - The label of the UDF.
     */
    public String getLabel() {
        return label;
    }
}

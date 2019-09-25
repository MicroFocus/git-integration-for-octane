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

package com.microfocus.adm.almoctane.integration.git.config;

/**
 * This helps making the difference between Octane workspaces in different shared spaces.
 */
public class OctaneObject {
    private long workspace;
    private long sharedSpace;

    /**
     *
     * @param sharedSpace - the Octane shared space
     * @param workspace - the Octane workspace
     */
    public OctaneObject(long sharedSpace, long workspace) {
        this.sharedSpace = sharedSpace;
        this.workspace = workspace;
    }

    /**
     *
     * @param o - The Octane object.
     * @return  - true if the objects have the same shared space and workspace.
     *          - false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof OctaneObject) {
            OctaneObject octaneObject = (OctaneObject) o;
            return octaneObject.sharedSpace == this.sharedSpace && octaneObject.workspace == this.workspace;
        }
        return false;
    }

    /**
     * @return - The hash of the object, which is the workspace + sharedSpace number.
     */
    @Override
    public int hashCode() {
        return (int) (this.workspace + this.sharedSpace);
    }
}
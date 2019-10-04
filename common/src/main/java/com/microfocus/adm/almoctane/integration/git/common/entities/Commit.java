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
 * Class to modeling a commit
 */
public class Commit {
    private String hash;
    private String repoLink;

    /**
     * @param hash     - hash of the commit
     * @param repoLink - link to the repository from which the commit was cloned
     */
    public Commit(String hash, String repoLink) {
        this.hash = hash;
        this.repoLink = repoLink;
    }

    /**
     * @return - link to the repository from which the commit was cloned
     */
    public String getRepoLink() {
        return repoLink;
    }

    /**
     * @return - hash of the commit
     */
    public String getHash() {
        return this.hash;
    }
}

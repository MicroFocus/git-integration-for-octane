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

package com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.urls;

import com.google.api.client.http.GenericUrl;
import com.microfocus.adm.almoctane.integration.git.common.entities.Branch;

/**
 * Class used for creating the base URL to access the source code on a specific branch
 */
public class BitbucketBrowseCodeUrl extends GenericUrl {
    /**
     * @param server - The base branch information URL for the Bitbucket server
     */
    public BitbucketBrowseCodeUrl(String server) {
        super(server);
    }

    /**
     * @param branch - The Bitbucket branch.
     * @return - The new BitbucketBrowseCodeUrl with the branch path.
     */
    public BitbucketBrowseCodeUrl setBranch(Branch branch) {
        this.appendRawPath(String.format("%s?at=%s", this.getRawPath(), branch.getBranchId()));

        return this;
    }

    /**
     * @return - Clone of the current object
     */
    @Override
    public BitbucketBranchUtilsUrl clone() {
        return (BitbucketBranchUtilsUrl) super.clone();
    }
}

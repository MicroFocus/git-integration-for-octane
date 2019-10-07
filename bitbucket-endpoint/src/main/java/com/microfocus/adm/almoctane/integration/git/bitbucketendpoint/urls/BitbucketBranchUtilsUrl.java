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
import com.google.api.client.util.Key;

/**
 * Class used for creating the base URL to make requests for branch utils to the Bitbucket server
 */
public class BitbucketBranchUtilsUrl extends GenericUrl {
    @Key
    protected int start;

    /**
     * @param server - The branch information URL for the Bitbucket server
     */
    public BitbucketBranchUtilsUrl(String server) {
        super(server + "/rest/branch-utils/1.0");
    }

    /**
     * @return - The Bitbucket page from which to get the rest of the request response
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start - The Bitbucket page from which to get the rest of the request response
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return - Clone of the current object
     */
    @Override
    public BitbucketBranchUtilsUrl clone() {
        BitbucketBranchUtilsUrl clone = (BitbucketBranchUtilsUrl) super.clone();
        clone.start = this.start;
        return clone;
    }
}

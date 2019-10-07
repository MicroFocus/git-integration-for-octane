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
 * Class used for creating the base URL to make requests to the Bitbucket server
 */
public class BitbucketUrl extends GenericUrl {
    @Key
    protected int start;

    /**
     * @param server - the base Url for the Bitbucket server
     */
    public BitbucketUrl(String server) {
        super(server + "/rest/api/latest");
    }

    /**
     * @return - the Bitbucket page from which to get the rest of the request response
     */
    public int getStart() {
        return start;
    }

    /**
     * @param start - the Bitbucket page from which to get the rest of the request response
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return - clone of the current object
     */
    @Override
    public BitbucketUrl clone() {
        BitbucketUrl clone = (BitbucketUrl) super.clone();
        clone.start = this.start;
        return clone;
    }
}

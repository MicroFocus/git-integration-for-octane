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

package com.microfocus.adm.almoctane.integration.git.bitbucketendpoint.entity;

import com.google.api.client.util.Key;

import java.util.List;

/**
 * Class used to describe the "links" field of the Bitbucket response
 */
public class BitbucketLinks {
    @Key
    private List<BitbucketSelf> self;

    /**
     *
     * @return returns the link to self
     */
    public String getSelfLink() {
        return self.get(0).getHref();
    }
}

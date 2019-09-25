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
import com.microfocus.adm.almoctane.integration.git.common.entities.PullRequest;

import java.math.BigInteger;

/**
 * Class used to describe the pull request information received from the bitbucket response
 */
public class BitbucketPullRequest implements PullRequest {
    @Key
    private BitbucketLinks links;

    @Key("createdDate")
    private long createdLongDate;

    @Key("updatedDate")
    private long updatedLongDate;

    @Key
    private String title;

    @Key
    private String state;

    /**
     *
     * @return - link to this pull request
     */
    @Override
    public String getPullRequestLink() {
        return links.getSelfLink();
    }

    /**
     *
     * @return - the title of this pull request
     */
    @Override
    public String getPullRequestName() {
        return title;
    }

    /**
     *
     * @return - the state of this pull request
     */
    @Override
    public String getPullRequestState() {
        return state;
    }

    /**
     *
     * @return - the creation time of this pull request
     */
    public long getCreatedDate() {
        return createdLongDate;
    }


    /**
     *
     * @return - the last update time of this pull request
     */
    public long getUpdatedDate() {
        return updatedLongDate;
    }

    /**
     * The pull requests are ordered according to the last updated date just like in Bitbucket
     * @param o - pull request to compare with
     * @return -  0 if equal
     *           -1 if o was created before this
     *            1 if o was created after this or o is not a BitbucketPullRequest object
     *
     */
    @Override
    public int compareTo(PullRequest o) {
        if (o instanceof BitbucketPullRequest) {
            BitbucketPullRequest bitbucketPullRequest = (BitbucketPullRequest) o;
            if (bitbucketPullRequest.updatedLongDate == this.updatedLongDate)
                return 0;
            return this.updatedLongDate < bitbucketPullRequest.updatedLongDate ? 1 : -1;
        }
        return 1;
    }

    /**
     *
     * @param obj - object to compare with
     * @return - true if obj and this have the same pull request link and name
     *         - false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PullRequest) {
            PullRequest other = (PullRequest) obj;

            return other.getPullRequestLink().equals(this.getPullRequestLink()) && other.getPullRequestName().equals(this.getPullRequestName());
        }
        return false;
    }

    /**
     *
     * @return - hash of this pull request
     */
    @Override
    public int hashCode() {
        return new BigInteger(this.getPullRequestLink().getBytes()).intValue();
    }
}

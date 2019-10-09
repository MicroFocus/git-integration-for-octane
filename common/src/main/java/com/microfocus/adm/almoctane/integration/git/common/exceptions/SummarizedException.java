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

package com.microfocus.adm.almoctane.integration.git.common.exceptions;

import java.util.LinkedList;
import java.util.List;

/**
 * Base class for exceptions which can have an easy to understand summary.
 */
public abstract class SummarizedException extends RuntimeException {

    protected List<String> lineList;

    /**
     * @param message - message of the exception
     * @param cause   - cause of the exception
     */
    public SummarizedException(String message, Throwable cause) {
        super(message, cause);
        lineList = new LinkedList<>();
        lineList.add("Check the \"Git integration for Octane\" logs for more details.");
    }

    /**
     * @return - message of the exception including the summary
     */
    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder();
        message.append(super.getMessage()).append("\n\t\t");
        List<String> lineList = getSummary();
        lineList.forEach(line -> message.append(line).append("\n\t\t"));
        return message.toString();
    }

    /**
     * @return - a list of lines describing the exception
     */
    public List<String> getSummary() {
        return lineList;
    }
}

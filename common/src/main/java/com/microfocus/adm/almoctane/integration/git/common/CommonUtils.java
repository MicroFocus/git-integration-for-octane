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

import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtils {

    public static StringBuilder getLastModifiedHtmlString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        StringBuilder responseHtml = new StringBuilder("<html><body><p><b>Last updated on: ");
        //add the current time as the time of the last update
        responseHtml.append(formatter.format(date));
        responseHtml.append("</b></p>");
        return responseHtml;
    }
}

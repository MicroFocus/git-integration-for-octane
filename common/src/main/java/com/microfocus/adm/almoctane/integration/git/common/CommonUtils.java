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

package com.ews.log.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogPreprocessor {

    // Define regex patterns for key-value pairs and other variables
    private static final Pattern KV_PATTERN = Pattern.compile("([\\w]+)=([^\\s,]+)");

    public String preprocess(String log) {
        StringBuilder preprocessedLog = new StringBuilder();
        Matcher matcher = KV_PATTERN.matcher(log);

        // Replace variable values with placeholders
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);

            if (key.equals("eventId") || key.equals("key") || key.equals("messageType") || key.equals("messageId")) {
                preprocessedLog.append(key).append("={").append(value).append("}, ");
            } else {
                preprocessedLog.append(key).append("=").append(value).append(", ");
            }
        }
        return preprocessedLog.toString().trim();
    }


}



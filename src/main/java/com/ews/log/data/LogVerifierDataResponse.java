package com.ews.log.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LogVerifierDataResponse {
    List<Map<String, String>> logData = new ArrayList<>();

    public List<Map<String, String>> getLogData() {
        return logData;
    }

    public void setLogData(List<Map<String, String>> logData) {
        this.logData = logData;
    }
}

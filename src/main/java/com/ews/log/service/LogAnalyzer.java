package com.ews.log.service;
import com.ews.log.data.LogVerifierDataMessage;
import com.ews.log.data.LogVerifierDataRequest;
import com.ews.log.data.LogVerifierDataResponse;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {
    private DocumentCategorizerME categorizer;
    private LogPreprocessor preprocessor;

    public LogAnalyzer(File modelFile) throws IOException {
        try (FileInputStream modelIn = new FileInputStream(modelFile)) {
            DoccatModel model = new DoccatModel(modelIn);
            this.categorizer = new DocumentCategorizerME(model);
            this.preprocessor = new LogPreprocessor();
        }
    }

    public static Map<String, String> parseLogLine(String logLine) {
        Map<String, String> logMap = new HashMap<>();

        // Regular expression pattern to match key=value pairs
        Pattern pattern = Pattern.compile("(\\w+)=\\{?([^\\},]+)\\}?");
        Matcher matcher = pattern.matcher(logLine);

        // Iterate over all matches and put them into the map
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            logMap.put(key, value);
        }

        return logMap;
    }

    public String categorizeLog(String log) {
        // Preprocess the log into a string
        String preprocessedLog = preprocessor.preprocess(log);

        // Tokenize the preprocessed log (split by spaces)
        String[] tokens = preprocessedLog.split("\\s+");

        // Categorize using the tokens
        double[] outcomes = categorizer.categorize(tokens);
        return categorizer.getBestCategory(outcomes);
    }

    public LogVerifierDataResponse analyzeLogs(List<String> logs, LogVerifierDataRequest logVerifierDataRequest) {
        LogVerifierDataResponse logVerifierDataResponse = new LogVerifierDataResponse();
        List<Map<String, String>> logData = new ArrayList<>();

        for (String log : logs) {
            System.out.println("Log: " + log);

            String category = categorizeLog(log);
//            System.out.println("Log: " + log);
            System.out.println("Category: " + category);

          if ("datamessage-success".equals(category)) {
                Map<String, String> logMap = parseLogLine(log);
                for (LogVerifierDataMessage logVerifierDataMessage : logVerifierDataRequest.getLogVerifierDataMessages()) {
                    if (logMap.containsValue(logVerifierDataMessage.getKey()) && logMap.containsValue(logVerifierDataMessage.getMessageType())) {
                        logData.add(logMap);
                    }
                }


                // Handle informational logs
            } else if ("datamessage-failure".equals(category)) {
                // Handle informational logs
                Map<String, String> logMap = parseLogLine(log);
                for (LogVerifierDataMessage logVerifierDataMessage : logVerifierDataRequest.getLogVerifierDataMessages()) {
                    if (logMap.containsValue(logVerifierDataMessage.getKey()) && logMap.containsValue(logVerifierDataMessage.getMessageType())) {
                        logData.add(logMap);
                    }
                }
            }
        }
        logVerifierDataResponse.setLogData(logData);
        return logVerifierDataResponse;
    }
}


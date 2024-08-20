package com.ews.log.service;

import com.ews.log.data.LogVerifierDataMessage;
import com.ews.log.data.LogVerifierDataRequest;
import com.ews.log.data.LogVerifierDataResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DataMessageNLPLogProcessor {

    @Value("${log.file.name:/var/log/ZSP/payload.log}")
    private String logFilePath;
    @Value("classpath:data-message-model.bin")
    private Resource resource;

    @Value("${number.of.lines.to.read:500}")
    private int numberOfLines;

    public LogVerifierDataResponse verifyLogs(LogVerifierDataRequest logVerifierDataRequest) throws IOException {

        List<String> recentLogs = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LogAnalyzer analyzer = new LogAnalyzer(resource.getFile());
        LogPreprocessor preprocessor = new LogPreprocessor();

        try {
            List<String> last500Lines = readLastLines(logFilePath, 500);
            for (String line : last500Lines) {
                LocalDateTime logTime = extractTimestamp(line);
                if (logTime != null && isWithinLastTenSeconds(logTime, now)) {
                    String preprocessedLog = preprocessor.preprocess(line);
                    String logText = String.join(" ", preprocessedLog);
                    recentLogs.add(logText);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        LogVerifierDataResponse logVerifierDataResponse = analyzer.analyzeLogs(recentLogs,logVerifierDataRequest);
        verify(logVerifierDataResponse, getLogVerifierDataRequest());
        return logVerifierDataResponse;
    }
    private static void verify(LogVerifierDataResponse logVerifierDataResponse, LogVerifierDataRequest logVerifierDataRequest) {
        List<LogVerifierDataMessage> logVerifierDataMessages = logVerifierDataRequest.getLogVerifierDataMessages();
        List<Map<String, String>> logData = logVerifierDataResponse.getLogData();
        for(LogVerifierDataMessage logVerifierDataMessage : logVerifierDataMessages) {
            for(Map<String, String> logMap : logData) {
                if(logMap.containsValue(logVerifierDataMessage.getMessageType()))
                   if(logMap.get("key").equalsIgnoreCase(logVerifierDataMessage.getKey())
                           && logMap.get("messageType").equalsIgnoreCase(logVerifierDataMessage.getMessageType())){
                      "success".equalsIgnoreCase(logMap.get("dataMessageStatus"));
                }
            }
        }

    }

    private static LocalDateTime extractTimestamp(String log) {
        try {
            String timestamp = log.split(" ")[0];
            timestamp = timestamp.replace("[","").replace("]","");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return LocalDateTime.parse(timestamp, formatter);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isWithinLastTenSeconds(LocalDateTime logTime, LocalDateTime now) {
        //return !logTime.isBefore(now.minusSeconds(10)) && !logTime.isAfter(now);
        return true;
    }

    private static LogVerifierDataRequest getLogVerifierDataRequest(){
        LogVerifierDataRequest logVerifierDataRequest = new LogVerifierDataRequest();
        List<LogVerifierDataMessage> logVerifierDataMessages = new ArrayList<>();
        LogVerifierDataMessage logVerifierDataMessage= new LogVerifierDataMessage();
        logVerifierDataMessage.setMessageType("REJECTED_PAYMENT");
        logVerifierDataMessage.setKey("BOO16PKMTU90");
        logVerifierDataMessages.add(logVerifierDataMessage);
        logVerifierDataRequest.setLogVerifierDataMessages(logVerifierDataMessages);
        return (logVerifierDataRequest);
    }

    private static List<String> readLastLines(String filePath, int numLines) throws IOException {
        List<String> result = new LinkedList<>();
        try (RandomAccessFile fileHandler = new RandomAccessFile(filePath, "r")) {
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int lines = 0;

            for (long filePointer = fileLength; filePointer >= 0; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) { // New line character
                    if (sb.length() > 0) {
                        result.add(0, sb.reverse().toString());
                        sb = new StringBuilder();
                        lines++;
                        if (lines == numLines) {
                            break;
                        }
                    }
                } else if (readByte != 0xD) { // Carriage return
                    sb.append((char) readByte);
                }
            }

            if (sb.length() > 0) {
                result.add(0, sb.reverse().toString());
            }
        }
        return result;
    }
}


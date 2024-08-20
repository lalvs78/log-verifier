package com.ews.log.data;

import java.util.List;

public class LogVerifierDataRequest {
    public List<LogVerifierDataMessage> getLogVerifierDataMessages() {
        return logVerifierDataMessages;
    }

    public void setLogVerifierDataMessages(List<LogVerifierDataMessage> logVerifierDataMessages) {
        this.logVerifierDataMessages = logVerifierDataMessages;
    }

    private List<LogVerifierDataMessage> logVerifierDataMessages;
}

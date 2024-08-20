package com.ews.log.controler;

import com.ews.log.service.DataMessageNLPLogProcessor;
import com.ews.log.data.LogVerifierDataRequest;
import com.ews.log.data.LogVerifierDataResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("log/verification/")
public class LogVerificationController {

    @Autowired
    private DataMessageNLPLogProcessor dataMessageNLPLogProcessor;

    @RequestMapping(method = RequestMethod.POST, value = "dataMessage", produces = "application/json")
    public LogVerifierDataResponse findKafkaLogsForDataMessage(HttpServletRequest httpServletRequest,
                          @RequestBody LogVerifierDataRequest logVerifierDataRequest) throws IOException {
        return dataMessageNLPLogProcessor.verifyLogs(logVerifierDataRequest);

    }
}

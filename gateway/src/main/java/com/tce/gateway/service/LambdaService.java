package com.tce.gateway.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;
import com.tce.gateway.dto.LambdaErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class LambdaService {

    private final BasicAWSCredentials credentials;

    private final Gson gson;

    @Autowired
    public LambdaService(BasicAWSCredentials credentials, Gson gson) {
        this.credentials = credentials;
        this.gson = gson;
    }

    public String invoke(String functionName, String payload) {
        InvokeRequest invokeRequest = new InvokeRequest()
                .withFunctionName(functionName)
                .withPayload(gson.toJson(payload));
        InvokeResult invokeResult;

        AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_SOUTH_1).build();

        invokeResult = awsLambda.invoke(invokeRequest);

        byte[] array = invokeResult.getPayload().array();

        System.out.println("Status code " + invokeResult.getStatusCode());

        final String responseString = new String(array, StandardCharsets.UTF_8);
        System.out.println("Response " + responseString);

        if (StringUtils.isNullOrEmpty(invokeResult.getFunctionError())) {
            return responseString;
        }

        final LambdaErrorResponse errorResponse = gson.fromJson(responseString, LambdaErrorResponse.class);
        log.info("Error Response " + errorResponse);
        throw new RuntimeException(errorResponse.getErrorMessage());
    }

}

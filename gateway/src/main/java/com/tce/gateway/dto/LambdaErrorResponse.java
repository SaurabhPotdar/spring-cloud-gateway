package com.tce.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Error response from <a href="https://docs.aws.amazon.com/lambda/latest/dg/nodejs-exceptions.html">Node JS</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LambdaErrorResponse {

    private String errorType;
    private String errorMessage;
    private String[] trace;

}

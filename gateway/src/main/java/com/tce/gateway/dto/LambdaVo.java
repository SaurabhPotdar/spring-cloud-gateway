package com.tce.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class LambdaVo {

    @Getter @Setter
    private String functionName;

    @Getter @Setter
    private String jsonPayload;

}
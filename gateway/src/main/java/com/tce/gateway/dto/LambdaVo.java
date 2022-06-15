package com.tce.gateway.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LambdaVo {

    @Getter
    @Setter
    private String functionName;

    @Getter
    @Setter
    private String jsonPayload;

}
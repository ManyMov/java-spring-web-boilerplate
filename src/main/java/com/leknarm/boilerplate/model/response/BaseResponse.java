package com.leknarm.boilerplate.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse<T> {
    @Builder.Default
    private int code = 200;

    @Builder.Default
    private String message = "success";
    private T data;
}

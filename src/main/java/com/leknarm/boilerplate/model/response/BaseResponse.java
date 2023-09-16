package com.leknarm.boilerplate.model.response;

import lombok.Builder;
import lombok.Data;
import lombok.Generated;

@Data
@Builder
@Generated
public class BaseResponse<T> {
    @Builder.Default
    private int code = 200;

    @Builder.Default
    private String message = "success";
    private T data;
}

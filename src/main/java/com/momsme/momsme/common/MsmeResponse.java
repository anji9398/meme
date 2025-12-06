package com.momsme.momsme.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class MsmeResponse<T> {
    private CharSequence message;
    private Object data;
    private Integer statusCode;
    private String status;
}

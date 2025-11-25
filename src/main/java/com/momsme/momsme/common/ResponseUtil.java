package com.momsme.momsme.common;

public class ResponseUtil {

    public static <T> MsmeResponse<T> success(CharSequence message, T data) {
        return MsmeResponse.<T>builder()
                .message(message)
                .data(data)
                .statusCode(200)
                .status("SUCCESS")
                .build();
    }

    public static <T> MsmeResponse<T> created(CharSequence message, T data) {
        return MsmeResponse.<T>builder()
                .message(message)
                .data(data)
                .statusCode(201)
                .status("SUCCESS")
                .build();
    }

    public static MsmeResponse<?> error(CharSequence message, int statusCode) {
        return MsmeResponse.builder()
                .message(message)
                .data(null)
                .statusCode(statusCode)
                .status("FAILED")
                .build();
    }
}


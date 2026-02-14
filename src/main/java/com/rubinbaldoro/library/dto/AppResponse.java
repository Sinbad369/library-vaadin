package com.rubinbaldoro.library.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppResponse<T> {
    private T data;
    private String message;
    private boolean success;

    public AppResponse(String message) {
        this.message = message;
        this.success = true;
    }

    public AppResponse(T data) {
        this.data = data;
        this.success = true;
    }

    public AppResponse(T data, String message) {
        this.data = data;
        this.message = message;
        this.success = true;
    }
}
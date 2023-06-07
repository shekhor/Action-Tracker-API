package com.tigerit.soa.loginsecurity.util.core;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum Status {
    SUCCESS,
    FAILED,
    PROCESSING,
    ACTIVE,
    INACTIVE,
    DELETED;

    public static Status getStatus(String status) {
        return Arrays.stream(Status.values())
                .filter(v -> v.name().equals(status))
                .findFirst()
                .orElse(null);
    }
}


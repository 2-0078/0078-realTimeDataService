package com.pieceofcake.real_time_data.common.exception;

import com.pieceofcake.real_time_data.common.entity.BaseResponseStatus;
import lombok.Getter;

@Getter
public class BaseException extends RuntimeException {

    private final BaseResponseStatus status;

    public BaseException(BaseResponseStatus status) {
        this.status = status;
    }
}

package net.zjitc.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

// 业务异常
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException{

    private Integer code;
    private String message;

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}

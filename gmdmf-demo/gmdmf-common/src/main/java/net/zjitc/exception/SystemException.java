package net.zjitc.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

// 系统异常
@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException{

    private Integer code;
    private String message;

    public SystemException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public SystemException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}

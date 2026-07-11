package net.zjitc.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 必须添加 data 注解，不然不能成功的转为 json 格式，会报 406 错误
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public static <E> Result<E> success(Integer code) {
        return new Result<>(code, "操作成功", null);
    }

    public static <E> Result<E> success(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <E> Result<E> success(Integer code, String message, E data) {
        return new Result<>(code, message, data);
    }

    public static <E> Result<E> error(Integer code) {
        return new Result<>(code, "操作失败", null);
    }

    public static <E> Result<E> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}

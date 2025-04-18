package com.dyl.fruitstock.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务异常, 非强制检查
 * @author benx
 */

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@Data
public class BusinessException extends RuntimeException {
    private final int code;
    private final String message;


}

package com.jk.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String exMessage = ex.getMessage();
        log.error(exMessage);

        if (exMessage.contains("Duplicate entry")) {
            return R.error(ex.getMessage().split(" ")[2] + "已存在");
        }

        return R.error("网络异常");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        String exMessage = ex.getMessage();
        log.error(exMessage);

        return R.error(ex.getMessage());
    }
}

package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常捕获类
 */
@ControllerAdvice
public class ExceptionCatch {
    private static final Logger LOGGER= LoggerFactory.getLogger(ExceptionCatch.class);
    //自定义map集合，key为一个异常类，值为错误代码
    //使用EXCEPTIONS存放异常类型和错误代码的映射，ImmutableMap的特点的一旦创建不可改变，并且线程安全
    private static ImmutableMap<Class<? extends Throwable>,ResultCode > EXCEPTIONS;
   //使用builder来构建一个异常类型和错误代码的异常
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder=ImmutableMap.builder();
    //捕获CustomException异常

    /**
     * 可预知自定义异常
     * @param e
     * @return
     */
   @ExceptionHandler(CustomException.class)
   @ResponseBody
    public ResponseResult customException(CustomException e){
        LOGGER.error("catch exception : {}\r\nexception: ",e.getMessage(),e);
       ResultCode resultCode=e.getResultCode();
       ResponseResult responseResult=new ResponseResult(resultCode);
       return responseResult;
    }

    /**
     * 不可预知异常处理
     * @param exception
     * @return
     */
    //捕获Exception异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception exception){
        LOGGER.error("catch exception : {}\r\nexception: ",exception.getMessage(),exception);
        if(EXCEPTIONS==null)
            EXCEPTIONS=builder.build();
            //获取EXCEPTIONS的返回值ResultCode
            final ResultCode resultCode=EXCEPTIONS.get(exception.getClass());
            //只允许创建一个responseResult对象
            final ResponseResult responseResult;
            if(resultCode!=null){
               responseResult = new ResponseResult(resultCode);
            }else {
               responseResult = new ResponseResult(CommonCode.SERVER_ERROR);
            }
        return responseResult;
    }
    static {
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }
}

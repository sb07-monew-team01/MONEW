package com.codeit.monew.global.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecution {

    LogTag tag() default LogTag.NONE;

    //태그용도
    String value() default "";

    //시간용도
    boolean time() default true;

// 사용법  로그넣고싶은 클래스,메서드위에 @LogExecution 붙이면된다
// 중첩 시 메서드 우선으로 적용
// 시간은 디폴트 적용

// @LogExecution --그냥확인
// [START] ClassName.method()
// [END] ClassName.method() (123 ms)
// [ERROR] ClassName.method() (123 ms) args=[request=UserLoginRequest[email=qlqhroal@naver.com, password=123213121!!@#ww]]

// @LogExecution(tag = "LogTag.User")
// [START] [User] ClassName.method()
// [SUCCESS] [User] ClassName.method() (123 ms)
// [ERROR] [User] ClassName.method() (123 ms) args=[request=UserLoginRequest[email=qlqhroal@naver.com, password=123213121!!@#ww]]

// @LogExecution(value = "String", time = false)
// [START] [String]  UserController.login(..)
// [SUCCESS] [String]  UserController.login(..)
// [ERROR] [String] UserController.login(..)  args=[request=UserLoginRequest[email=qlqhroal@naver.com, password=123213121!!@#ww]]

}

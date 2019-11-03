package com.example.wocaowocao.Base;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)// 运行时注解
@Target(TYPE)//类接口注解
public @interface ViewInject {
    int main_layout_id() default -1;
}

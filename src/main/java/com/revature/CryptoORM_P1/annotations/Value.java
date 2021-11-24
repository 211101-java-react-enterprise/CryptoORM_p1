package com.revature.CryptoORM_P1.annotations;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    public String correspondingColumn() default "";
}

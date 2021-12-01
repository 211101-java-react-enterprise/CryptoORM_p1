package com.revature.CryptoORM_P1.annotations;

import java.lang.annotation.*;

/**
 *      Used to mark the get methods for getting the values of columns
 */


@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {
    public String correspondingColumn() default "";
}

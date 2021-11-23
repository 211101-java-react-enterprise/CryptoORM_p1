package com.revature.CryptoORM_P1.mapper;

import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.exception.InvalidClassException;

import java.lang.reflect.Field;

public class Mapper {

    StringBuilder builder = new StringBuilder("");

    public boolean insert(Object obj) throws InvalidClassException {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table;
        Field[] fields;

        if (inputClass.isAnnotationPresent(Table.class)) {
            table = (Table)inputClass.getAnnotation(Table.class);
        } else {
            throw new InvalidClassException("Class provided is missing Table annotation!");
        }


        return false;
    }
}

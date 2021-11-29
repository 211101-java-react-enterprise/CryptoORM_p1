package com.revature.CryptoORM_P1.mapper;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.annotations.Value;
import com.revature.CryptoORM_P1.exception.InvalidClassException;
import com.revature.CryptoORM_P1.exception.MethodInvocationException;
import com.revature.CryptoORM_P1.models.User;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collector;

public class SQLMapper {

    StringBuilder builder = new StringBuilder("");

    public boolean insert(Object obj) throws InvalidClassException, MethodInvocationException {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table;
        Field[] fields;
        Method[] methods;

        ArrayList<Column> columns = new ArrayList<>();

        if (inputClass.isAnnotationPresent(Table.class)) {
            table = (Table)inputClass.getAnnotation(Table.class);
        } else {
            throw new InvalidClassException("Class provided is missing Table annotation!");
        }

        fields = inputClass.getDeclaredFields();

        methods = Arrays.stream(inputClass.getMethods())
                                        .filter(m -> m.isAnnotationPresent(Value.class))
                                        .toArray(Method[]::new);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                columns.add(field.getAnnotation(Column.class));
            }
        }

        if (columns.isEmpty()) {
            throw new InvalidClassException("Class provided has no Column Annotations!");
        }

        builder.setLength(0);

        builder.append("insert into " + table.tableName() + " (");
        for (Column column : columns) {
            builder.append(column.columnName() + ", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(") values ");
        for (int i = 0; i < methods.length; i++) {
            try {
                for (Method method : methods) {
                    if (method.getAnnotation(Value.class).correspondingColumn()
                                .equals(fields[i].getAnnotation(Column.class).columnName())) {
                        builder.append(method.invoke(obj) + ", ");
                        break;
                    }
                }
            }  catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new MethodInvocationException("There was an error when attempting to invoke obj's methods");
            }
        }
        builder.setLength(builder.length() - 2);
        builder.append(";");

        System.out.println(builder);

        return true;
    }

}

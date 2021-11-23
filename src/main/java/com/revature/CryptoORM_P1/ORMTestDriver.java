package com.revature.CryptoORM_P1;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.models.User;

import java.lang.reflect.Field;

public class ORMTestDriver {

    public static void main(String[] args) {
        Class userClass = User.class;
        if (userClass.isAnnotationPresent(Table.class)) {
            Table table = (Table)userClass.getAnnotation(Table.class);
            System.out.println(table.tableName());
        }

        Field[] fields = userClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = (Column)field.getAnnotation(Column.class);
                System.out.println(column.columnName());
            }
        }
    }
}

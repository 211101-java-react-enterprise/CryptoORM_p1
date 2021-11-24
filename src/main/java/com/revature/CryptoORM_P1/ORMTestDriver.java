package com.revature.CryptoORM_P1;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.mapper.SQLMapper;
import com.revature.CryptoORM_P1.models.Test;
import com.revature.CryptoORM_P1.models.User;

import java.lang.reflect.Field;
import java.util.UUID;

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
                Column column = field.getAnnotation(Column.class);
                System.out.println(column.columnName());
            }
        }

        System.out.println("\n-------------------------\n");

        SQLMapper mapper = new SQLMapper();
        Test test = new Test("TestValue", "TestValue", 1.12);
        User newUser = new User("usernameValue", "p4ssword");
        newUser.setUserUUID(UUID.randomUUID().toString());

        mapper.insert(test);
        mapper.insert(newUser);

    }
}

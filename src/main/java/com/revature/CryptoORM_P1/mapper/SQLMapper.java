package com.revature.CryptoORM_P1.mapper;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.annotations.Value;
import com.revature.CryptoORM_P1.exception.InvalidClassException;
import com.revature.CryptoORM_P1.exception.MethodInvocationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public class SQLMapper {

    StringBuilder builder = new StringBuilder("");

    /**
     *Takes in generic, properly annotated object and returns SQL insert string
     * Throws exception if object is not correctly annotated
     */
    public String insert(Object obj) throws InvalidClassException, MethodInvocationException {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        builder.setLength(0);

        builder.append("insert into " + table.tableName() + " (");

        for (String column : columnData.get(0)) {
            builder.append(column + ", ");
        }

        builder.setLength(builder.length() - 2);

        builder.append(") values ");

        for (String value : columnData.get(1)) {
            builder.append("'" + value + "', ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(";");

        System.out.println(builder);

        return builder.toString();
    }

    /**
     *Takes in generic, properly annotated object and returns SQL update string
     * Throws exception if object is not correctly annotated
     */
    public String update(Object obj, String idColumnName) {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        builder.setLength(0);

        builder.append("update " + table.tableName() + " set ");

        for (int i = 0; i < columnData.get(0).size(); i++) {
            builder.append(columnData.get(0).get(i) + " = '" + columnData.get(1).get(i) + "', ");
        }

        builder.setLength(builder.length() - 2);

        builder.append(" where " + idColumnName + " = '");
        for (int i = 0; i < columnData.get(0).size(); i++) {
            if (columnData.get(0).get(i).equals(idColumnName)) {
                builder.append(columnData.get(1).get(i));
                break;
            }
        }

        builder.append("';");

        System.out.println(builder);

        return builder.toString();
    }

    /**
     *Takes in generic, properly annotated object and returns SQL select string to be used in joins method
     * Throws exception if object is not correctly annotated
     */
    public String select (Object obj, String... columns) {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        builder.setLength(0);
        builder.append("select * from " + table.tableName()+" where "+buildColumnValuePairs(columns, columnData));
        builder.append(";");

        return builder.toString();
    }

    /**
     *Takes in generic, properly annotated object and returns SQL select string to be used in joins method
     * Throws exception if object is not correctly annotated
     */
    public String delete (Object obj, String... columns) {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        builder.setLength(0);

        builder.append("delete from " + table.tableName()+" where "+buildColumnValuePairs(columns, columnData));
        builder.append(";");

        return builder.toString();
    }

    /**
     * Used by delete and select method in where clauses of SQL statements. Returns sql formatted string
     */
    private String buildColumnValuePairs(String[] columns, ArrayList<ArrayList<String>> columnData) {
        String result = "";
        for (int i = 0; i < columns.length; i++) {
            for (int k = 0; k < columnData.get(0).size(); k++) {
                if (columnData.get(0).get(k).equals(columns[i])) {
                    result += columns[i] + " = '" + columnData.get(1).get(k)+"'";
                    if(i != columns.length-1){
                        result +=", ";
                    }
                    break;
                }
            }
        }
        return result;
    }

    private Table getTable(Class inputClass) {
        if (inputClass.isAnnotationPresent(Table.class)) {
            return (Table)inputClass.getAnnotation(Table.class);
        } else {
            throw new InvalidClassException("Class provided is missing Table annotation!");
        }

    }

    /**
     *      Takes in obj and returns an arraylist of arraylist of string where
     *      arraylist.get(0) = columnName, and arraylist.get(1) = value
     * @param obj
     * @return
     */
    private ArrayList<ArrayList<String>> getColumnsAndValues(Object obj) {

        ArrayList<ArrayList<String>> result = new ArrayList<>();
        result.add(new ArrayList<>());
        result.add(new ArrayList<>());

        Class inputClass = obj.getClass();

        Field[] fields = inputClass.getDeclaredFields();

        Method[] methods = Arrays.stream(inputClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Value.class))
                .toArray(Method[]::new);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                result.get(0).add(field.getAnnotation(Column.class).columnName());
            }
        }

        if (result.get(0).isEmpty()) {
            throw new InvalidClassException("Class provided has no Column Annotations!");
        }

        for (int i = 0; i < methods.length; i++) {
            try {
                for (Method method : methods) {
                    if (method.getAnnotation(Value.class).correspondingColumn()
                            .equals(result.get(0).get(i))) {
                        result.get(1).add(method.invoke(obj).toString());
                        break;
                    }
                }
            }  catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new MethodInvocationException("There was an error when attempting to invoke obj's methods");
            }
        }
        return result;
    }


}

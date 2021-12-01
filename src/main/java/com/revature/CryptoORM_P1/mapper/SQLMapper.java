package com.revature.CryptoORM_P1.mapper;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.annotations.Value;
import com.revature.CryptoORM_P1.exception.InvalidClassException;
import com.revature.CryptoORM_P1.exception.MethodInvocationException;
import com.revature.CryptoORM_P1.util.ConnectionFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class SQLMapper {

    Connection conn;

    public SQLMapper(Properties props){
        ConnectionFactory.getInstance().addProperties(props);
        conn = ConnectionFactory.getInstance().getConnection();
    }

    StringBuilder builder = new StringBuilder("");

    /**
     *Takes in generic, properly annotated object and returns SQL insert string
     * Throws exception if object is not correctly annotated
     */
    public int insert(Object obj) throws InvalidClassException, MethodInvocationException {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);
        String statement = "";
        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        //StringBuilder builder = new StringBuilder();
        //builder.setLength(0);

        statement+="insert into " + table.tableName() + " (";
        int columnSize = columnData.get(0).size();
        for (int i = 0; i < columnSize; i++) {
            statement+= columnData.get(0).get(i);
            if(i < columnSize-1) statement+=", ";
        }

        //builder.setLength(builder.length() - 2);

        statement+= ") values (";

        for (int i = 0; i < columnSize; i++) {
            statement+= "?";
            if(i < columnSize-1) statement+=", ";
        }
        statement+=")";

        System.out.println(statement);
        try{
            PreparedStatement pstmt = conn.prepareStatement(statement);
            //int columnSize = columnData.get(0).size();
            for (int i = 0, j=1; j <= columnSize; i++, j++) {
//                System.out.println("pstmt: "+pstmt.toString());
//                System.out.println("size: "+columnData.get(1).size());
                switch (columnData.get(2).get(i)) {
                    case "v":
                        pstmt.setString(j, columnData.get(1).get(i));
                        break;
                    case "n":
                        pstmt.setDouble(j, Double.parseDouble(columnData.get(1).get(i)));
                        break;
                }
            }

            System.out.println(pstmt.toString());
            return pstmt.executeUpdate();
        } catch(Exception e){
            e.printStackTrace();

        }
        return -1;
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
    public ResultSet select (Object obj, String... columns) {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        String statement = "";

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        statement += "select * from " + table.tableName() + " where " + buildStatementWhereClause(columns);

        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);

            for (int i = 0; i < columns.length; i++) {
                for (int j = 0; j < columnData.get(1).size(); j++) {
                    if (columns[i].equals(columnData.get(0).get(j))) {
                        switch (columnData.get(2).get(j)) {
                            case "v":
                                pstmt.setString(j, columnData.get(1).get(j));
                                break;
                            case "n":
                                pstmt.setDouble(j, Double.parseDouble(columnData.get(1).get(j)));
                                break;
                        }
                        break;
                    }
                }
            }
            System.out.println(pstmt.toString() + "\n");

            return pstmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
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

        builder.append("delete from " + table.tableName()+" where "+ buildStatementWhereClause(columns));
        builder.append(";");

        return builder.toString();
    }

    /**
     *  Creates an SQL statement that joins 2 tables on a specified column
     *
     * @Params  objA - Primary object, generally contains ID to specify results
     *          tableB - class definition of table object, used for acquiring column and table names when necessary
     *          joinOnA - column of table a to join on
     *          joinOnB - column of table b to join on
     *          pK - used to determine value in objA that we are checking against (where clause)
     *          fK - provides column name to compare to in second table (where clause)
     * @return
     */
    public String joinSelect(Object objA, Class classB, String joinOnA, String joinOnB, String pK, String fK) {
        // select * from tableA a right join tableB b on a.joinonA = b.joinOnB where fk = pk;
        // Store necessary data from object
        Class inputClass = objA.getClass();
        Table tableA = getTable(inputClass);
        Table tableB = getTable(classB);
        String pkValue = "";

        Method[] methods = Arrays.stream(inputClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Value.class))
                .toArray(Method[]::new);

        try {
            for (Method method : methods) {
                if (method.getAnnotation(Value.class).correspondingColumn()
                        .equals(pK)) {
                    pkValue = method.invoke(objA).toString();
                    break;
                }
            }
        }  catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            throw new MethodInvocationException("There was an error when attempting to invoke obj's methods");
        }

        builder.setLength(0);
        builder.append("select * from " + tableA.tableName() + " a join " + tableB.tableName() +
                       " b on a." + joinOnA + " = b." + joinOnB + " where b." + fK + " = '" + pkValue + "';");

        return builder.toString();
    }

    /**
     * Used by delete and select method in where clauses of SQL statements. Returns sql formatted string
     */
    private String buildStatementWhereClause(String[] columns) {
        String result = "";
        for (int i = 0; i < columns.length; i++) {
            result += columns[i] + " = ? ";
            if(i != columns.length-1) result += "and ";
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
        result.add(new ArrayList<>());

        Class inputClass = obj.getClass();

        Field[] fields = inputClass.getDeclaredFields();

        Method[] methods = Arrays.stream(inputClass.getMethods())
                .filter(m -> m.isAnnotationPresent(Value.class))
                .toArray(Method[]::new);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                result.get(0).add(field.getAnnotation(Column.class).columnName());
                result.get(2).add(field.getAnnotation(Column.class).columnType());
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

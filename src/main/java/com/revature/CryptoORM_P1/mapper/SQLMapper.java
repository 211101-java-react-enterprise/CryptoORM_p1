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

    private static Connection conn;
    private static SQLMapper mapper = new SQLMapper();

    private SQLMapper(){
    }

    public static void setProperties(Properties props) throws SQLException {
        ConnectionFactory.getInstance().addProperties(props);
        conn = ConnectionFactory.getInstance().getConnection();
    }

    public static SQLMapper getInstance(){
        return mapper;
    }

    /**
     *Takes in generic, properly annotated object and returns SQL insert string
     * Throws exception if object is not correctly annotated
     */
    public int insert(Object obj) throws InvalidClassException, MethodInvocationException, SQLException {

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);
        String statement = "";
        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);


        statement+="insert into " + table.tableName() + " (";
        int columnSize = columnData.get(0).size();
        for (int i = 0; i < columnSize; i++) {
            statement+= columnData.get(0).get(i);
            if(i < columnSize-1) statement+=", ";
        }

        statement+= ") values (";

        for (int i = 0; i < columnSize; i++) {
            statement+= "?";
            if(i < columnSize-1) statement+=", ";
        }
        statement+=")";

        try{
            PreparedStatement pstmt = conn.prepareStatement(statement);
            for (int i = 0, j=1; j <= columnSize; i++, j++) {
                setValue(columnData.get(1).get(i), columnData.get(2).get(i), pstmt, j);
            }

            return pstmt.executeUpdate();
    } catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("failed to insert: SQLMapper#insert: " + e.getMessage());
        }
    }

    /**
     *Takes in generic, properly annotated object and returns SQL update string
     * Throws exception if object is not correctly annotated
     */
    public int update(Object obj, String... idColumnNames) throws InvalidClassException, MethodInvocationException, SQLException{

        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);
        String statement = "";
        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        statement+="update " + table.tableName() + " set ";
        int columnSize = columnData.get(0).size();
        for (int i = 0; i < columnSize; i++) {
            statement+=columnData.get(0).get(i) + " = ?";
            if(i < columnSize-1) statement+=", ";

        }

        statement+=" where " + buildStatementWhereClause(idColumnNames);

        try{
            PreparedStatement pstmt = conn.prepareStatement(statement);
            for (int i = 0, j=1; j <= columnSize; i++, j++) {
                setValue(columnData.get(1).get(i), columnData.get(2).get(i), pstmt, j);
            }
            for (int j = 0; j < idColumnNames.length; j++) {
                for (int i = 0; i < columnData.get(0).size(); i++) {
                    if (idColumnNames[j].equals(columnData.get(0).get(i))) {
                        setValue(columnData.get(1).get(i), columnData.get(2).get(i), pstmt, columnSize + j + 1);
                    }
                }
            }

            return pstmt.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
            throw new SQLException("failed to update: SQLMapper#update: " + e.getMessage());
        }
    }

    /**
     *Takes in generic, properly annotated object and returns SQL select string to be used in joins method
     * Throws exception if object is not correctly annotated
     */
    public ResultSet select (Object obj, String... columns) throws InvalidClassException, MethodInvocationException, SQLException{

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        String statement = "";

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        statement += "select * from " + table.tableName();
        if (columns.length != 0) {
            statement += " where " + buildStatementWhereClause(columns);
        }


        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);

            for (int i = 0; i < columns.length; i++) {
                for (int j = 0; j < columnData.get(1).size(); j++) {
                    if (columns[i].equals(columnData.get(0).get(j))) {
                        setValue(columnData.get(1).get(j), columnData.get(2).get(j), pstmt, i+1);
                        break;
                    }
                }
            }

            return pstmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to select: SQLMapper#select: " + e.getMessage());
        }
    }

    /**
     *Takes in generic, properly annotated object and returns SQL select string to be used in joins method
     * Throws exception if object is not correctly annotated
     */
    public int delete (Object obj, String... columns) throws InvalidClassException, MethodInvocationException, SQLException{

        // Store necessary data from object
        Class inputClass = obj.getClass();
        Table table = getTable(inputClass);

        ArrayList<ArrayList<String>> columnData = getColumnsAndValues(obj);

        String statement = "delete from " + table.tableName() + " where " + buildStatementWhereClause(columns);
        int columnSize = columnData.get(0).size();
        try {
            PreparedStatement pstmt = conn.prepareStatement(statement);
            for(int k = 0; k< columns.length; k++){
                for (int i = 0; i < columnSize; i++) {
                    if(columns[k].equals(columnData.get(0).get(i))) {
                        setValue(columnData.get(1).get(i), columnData.get(2).get(i), pstmt, k + 1);
                        break;
                    }
                }
            }
            int status = pstmt.executeUpdate();
            if(status == 0) return -1;//return -1 if there is no element to delete
            else return status;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Failed to delete: SQLMapper#delete: " + e.getMessage());
        }
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
    public ResultSet joinSelect(Object objA, Class classB, String joinOnA, String joinOnB, String pK, String fK)throws InvalidClassException, MethodInvocationException, SQLException {
        // select * from tableA a right join tableB b on a.joinonA = b.joinOnB where fk = pk;
        // Store necessary data from object
        Class inputClass = objA.getClass();
        Table tableA = getTable(inputClass);
        Table tableB = getTable(classB);
        String pkValue = "";
        String pkType = "";
        Field[] fields = objA.getClass().getDeclaredFields();
        //get type for pk
        for(int i = 0; i < fields.length; i++){
            if (fields[i].isAnnotationPresent(Column.class)&&
                    fields[i].getAnnotation(Column.class).columnName().equals(pK))
            {
                pkType = fields[i].getAnnotation(Column.class).columnType();
                break;
            }
        }

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
        String statement = "select * from " + tableA.tableName() + " a join " + tableB.tableName() +
                " b on a." + joinOnA + " = b." + joinOnB + " where b." + fK + " = ?;";

        try{
            PreparedStatement pstmt = conn.prepareStatement(statement);

            setValue(pkValue, pkType, pstmt, 1);
            return pstmt.executeQuery();
        } catch(Exception e){
            e.printStackTrace();
            throw new SQLException("Failed to join: unable to update prepared statement or query");
        }
    }


    /**
     *      Used when setting SQL values in PreparedStatement, repetitive logic
     * @param value
     * @param type
     * @param pstmt
     * @param index
     * @throws SQLException
     */
    private void setValue(String value, String type, PreparedStatement pstmt, int index) throws SQLException {
        switch (type) {
            case "v":
                pstmt.setString(index, value);
                break;
            case "n":
                pstmt.setDouble(index, Double.parseDouble(value));
                break;
        }
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

    private Table getTable(Class inputClass) throws InvalidClassException {
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
    private ArrayList<ArrayList<String>> getColumnsAndValues(Object obj) throws MethodInvocationException {

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

                        Object value = method.invoke(obj);
                        if (value != null) {
                            result.get(1).add(value.toString());
                        } else {
                            result.get(1).add("");
                        }
                        break;
                    }
                }
            }  catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
                throw new MethodInvocationException("SQLMapper#getColumnsAndValue: There was an error when attempting to invoke obj's methods");
            }
        }
        return result;
    }


}

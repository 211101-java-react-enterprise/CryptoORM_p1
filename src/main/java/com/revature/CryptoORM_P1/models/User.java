package com.revature.CryptoORM_P1.models;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;

/**
 *  User class is a simple data model used to store information relevant to
 *  a user
 *
 *  it stores username and password as well as a unique UUID
 *
 *  class primarily consists of simple getters and setters
 */
@Table(tableName = "user")
public class User {

    //000000000000000000000000000000000000000000000000000000000000000000000000000000000

    @Column(columnName = "user_uuid")
    private String userUUID;
    @Column(columnName = "username")
    private String username;
    @Column(columnName = "password")
    private String password;

    //000000000000000000000000000000000000000000000000000000000000000000000000000000000

    //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

    public User(String username, String password) {
        this.username = username;
        this.password = password;

    }

    //CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC

    //---------------------------------------------------------------------------------

    public String getUserUUID() {
        return userUUID;
    }
    public void setUserUUID(String userUUID) {
        this.userUUID = userUUID;
    }

    //----------------------------------------------------------------------------------

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    //-----------------------------------------------------------------------------------

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

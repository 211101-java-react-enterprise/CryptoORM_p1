package com.revature.CryptoORM_P1.models;

import com.revature.CryptoORM_P1.annotations.Column;
import com.revature.CryptoORM_P1.annotations.Table;
import com.revature.CryptoORM_P1.annotations.Value;

@Table(tableName = "test")
public class Test {

    @Column(columnName = "name")
    String name;
    @Column(columnName = "test")
    String test;
    @Column(columnName = "double_num")
    double dubNum;

    public Test(String name, String test, double dubNum) {
        this.name = name;
        this.test = test;
        this.dubNum = dubNum;
    }

    @Value(correspondingColumn = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Value(correspondingColumn = "test")
    public String getTest() {
        return test;
    }
    public void setTest(String test) {
        this.test = test;
    }

    @Value(correspondingColumn = "double_num")
    public double getDubNum() {
        return dubNum;
    }
    public void setDubNum(double dubNum) {
        this.dubNum = dubNum;
    }
}

package com.revature.CryptoORM_P1.exception;

public class InvalidSQLRequestException extends RuntimeException{
    public InvalidSQLRequestException(String msg){
        super(msg);
    }
}

package com.gmail.ia.reader.infraestructure.advicers.exceptions;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message){
        super(message);
    }
}
